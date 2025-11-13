import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { AuthService } from './auth.service';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

export interface MensajeRequest {
  receptorId: number;
  alojamientoId: number;
  contenido: string;
}

export interface MensajeResponse {
  id: number;
  emisorId: number;
  emisorNombre: string;
  receptorId: number;
  receptorNombre: string;
  alojamientoId: number;
  alojamientoNombre: string;
  contenido: string;
  fechaEnvio: string;
  leido: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/mensajes';
  private wsUrl = 'http://localhost:8080/ws-mensajes';

  private stompClient: any;
  private mensajesSubject = new BehaviorSubject<MensajeResponse[]>([]);
  private mensajesNoLeidosSubject = new BehaviorSubject<number>(0);

  mensajes$ = this.mensajesSubject.asObservable();
  mensajesNoLeidos$ = this.mensajesNoLeidosSubject.asObservable();

  private conversacionActual: {
    otroUsuarioId?: number,
    alojamientoId?: number
  } = {};

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  // Conectar al WebSocket
  conectar(): void {
    const usuario = this.authService.getUser();
    if (!usuario) return;

    const socket = new SockJS(this.wsUrl);
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect(
      {
        'Authorization': `Bearer ${this.authService.getToken()}`
      },
      (frame: any) => {
        console.log('Conectado a WebSocket: ' + frame);

        // Suscribirse a mensajes personales
        this.stompClient.subscribe(`/user/${usuario.id}/topic/mensajes`, (mensaje: any) => {
          const mensajeRecibido: MensajeResponse = JSON.parse(mensaje.body);
          this.procesarMensajeRecibido(mensajeRecibido);
        });

        // Suscribirse a notificaciones de mensajes leídos
        this.stompClient.subscribe(`/user/${usuario.id}/topic/mensajes.leidos`, (mensaje: any) => {
          const mensajeLeido: MensajeResponse = JSON.parse(mensaje.body);
          this.actualizarEstadoMensaje(mensajeLeido);
        });

        // Cargar mensajes no leídos al conectar
        this.contarMensajesNoLeidos().subscribe(cantidad => {
          this.mensajesNoLeidosSubject.next(cantidad);
        });
      },
      (error: any) => {
        console.error('Error al conectar con WebSocket:', error);
      }
    );
  }

  // Desconectar del WebSocket
  desconectar(): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect();
      console.log('Desconectado de WebSocket');
    }
  }

  // Enviar mensaje a través de WebSocket
  enviarMensaje(mensaje: MensajeRequest): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send('/app/chat.enviar', {}, JSON.stringify(mensaje));
    } else {
      console.error('No hay conexión WebSocket. Enviando por HTTP...');
      this.enviarMensajeHttp(mensaje).subscribe(
        (respuesta) => {
          this.procesarMensajeRecibido(respuesta);
        },
        (error) => {
          console.error('Error al enviar mensaje por HTTP:', error);
        }
      );
    }
  }

  // Enviar mensaje a través de HTTP (fallback)
  enviarMensajeHttp(mensaje: MensajeRequest): Observable<MensajeResponse> {
    return this.http.post<MensajeResponse>(this.apiUrl, mensaje);
  }

  // Obtener conversación entre dos usuarios sobre un alojamiento
  obtenerConversacion(otroUsuarioId: number, alojamientoId: number): Observable<MensajeResponse[]> {
    this.conversacionActual = { otroUsuarioId, alojamientoId };
    return this.http.get<MensajeResponse[]>(`${this.apiUrl}/conversacion?otroUsuarioId=${otroUsuarioId}&alojamientoId=${alojamientoId}`);
  }

  // Cargar conversación y actualizar el subject
  cargarConversacion(otroUsuarioId: number, alojamientoId: number): void {
    this.obtenerConversacion(otroUsuarioId, alojamientoId).subscribe(
      (mensajes) => {
        this.mensajesSubject.next(mensajes);

        // Marcar mensajes como leídos
        const usuario = this.authService.getUser();
        if (usuario) {
          const mensajesNoLeidos = mensajes.filter(m =>
            m.receptorId === usuario.id &&
            m.emisorId === otroUsuarioId &&
            !m.leido
          );

          if (mensajesNoLeidos.length > 0) {
            this.marcarComoLeidos(otroUsuarioId, alojamientoId);
          }
        }
      },
      (error) => {
        console.error('Error al cargar conversación:', error);
      }
    );
  }

  // Marcar mensajes como leídos
  marcarComoLeidos(emisorId: number, alojamientoId: number): void {
    if (this.stompClient && this.stompClient.connected) {
      const mensaje = {
        emisorId: emisorId,
        alojamientoId: alojamientoId
      };
      this.stompClient.send('/app/chat.marcarLeido', {}, JSON.stringify(mensaje));
    } else {
      this.http.put<number>(`${this.apiUrl}/marcar-leidos?emisorId=${emisorId}&alojamientoId=${alojamientoId}`, {})
        .subscribe(
          () => {
            this.actualizarMensajesLeidos(emisorId);
            this.contarMensajesNoLeidos().subscribe(cantidad => {
              this.mensajesNoLeidosSubject.next(cantidad);
            });
          },
          (error) => {
            console.error('Error al marcar mensajes como leídos:', error);
          }
        );
    }
  }

  // Contar mensajes no leídos
  contarMensajesNoLeidos(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/no-leidos/count`);
  }

  // Obtener alojamientos con conversaciones
  obtenerAlojamientosConConversaciones(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/alojamientos`);
  }

  // Obtener usuarios en conversación
  obtenerUsuariosEnConversacion(alojamientoId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/usuarios?alojamientoId=${alojamientoId}`);
  }

  // Procesar mensaje recibido
  private procesarMensajeRecibido(mensaje: MensajeResponse): void {
    const mensajes = this.mensajesSubject.value;
    const usuario = this.authService.getUser();

    // Verificar si el mensaje pertenece a la conversación actual
    if (this.conversacionActual.otroUsuarioId && this.conversacionActual.alojamientoId) {
      const esConversacionActual =
        mensaje.alojamientoId === this.conversacionActual.alojamientoId &&
        ((mensaje.emisorId === this.conversacionActual.otroUsuarioId && mensaje.receptorId === usuario.id) ||
         (mensaje.receptorId === this.conversacionActual.otroUsuarioId && mensaje.emisorId === usuario.id));

      if (esConversacionActual) {
        // Añadir mensaje a la conversación actual
        const mensajeExistente = mensajes.find(m => m.id === mensaje.id);
        if (!mensajeExistente) {
          this.mensajesSubject.next([...mensajes, mensaje]);
        }

        // Si somos el receptor, marcar como leído
        if (mensaje.receptorId === usuario.id && !mensaje.leido) {
          this.marcarComoLeidos(mensaje.emisorId, mensaje.alojamientoId);
        }
      }
    }

    // Actualizar contador de mensajes no leídos
    if (mensaje.receptorId === usuario.id && !mensaje.leido) {
      this.contarMensajesNoLeidos().subscribe(cantidad => {
        this.mensajesNoLeidosSubject.next(cantidad);
      });
    }
  }

  // Actualizar estado de mensaje
  private actualizarEstadoMensaje(mensaje: MensajeResponse): void {
    const mensajes = this.mensajesSubject.value;
    const mensajesActualizados = mensajes.map(m => {
      if (m.id === mensaje.id) {
        return { ...m, leido: mensaje.leido };
      }
      return m;
    });
    this.mensajesSubject.next(mensajesActualizados);
  }

  // Actualizar mensajes leídos
  private actualizarMensajesLeidos(emisorId: number): void {
    const mensajes = this.mensajesSubject.value;
    const usuario = this.authService.getUser();

    const mensajesActualizados = mensajes.map(m => {
      if (m.emisorId === emisorId && m.receptorId === usuario.id) {
        return { ...m, leido: true };
      }
      return m;
    });

    this.mensajesSubject.next(mensajesActualizados);
  }
}
