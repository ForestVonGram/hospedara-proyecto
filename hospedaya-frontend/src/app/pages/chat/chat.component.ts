import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ChatService, MensajeResponse } from '../../services/chat.service';
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { AlojamientoService } from '../../services/alojamiento.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class ChatComponent implements OnInit, OnDestroy {
  mensajes: MensajeResponse[] = [];
  alojamientoId: number | null = null;
  otroUsuarioId: number | null = null;
  usuarioActual: any;
  otroUsuario: any = null;
  alojamiento: any = null;
  nuevoMensaje: string = '';
  cargando: boolean = true;

  private subscriptions: Subscription[] = [];

  constructor(
    private chatService: ChatService,
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private alojamientoService: AlojamientoService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.usuarioActual = this.authService.getUser();

    if (!this.usuarioActual) {
      this.router.navigate(['/login']);
      return;
    }

    // Conectar al WebSocket
    this.chatService.conectar();

    // Suscribirse a los mensajes
    this.subscriptions.push(
      this.chatService.mensajes$.subscribe(mensajes => {
        this.mensajes = mensajes;
        this.cargando = false;
      })
    );

    // Obtener parámetros de la URL
    this.subscriptions.push(
      this.route.params.subscribe(params => {
        if (params['alojamientoId'] && params['usuarioId']) {
          this.alojamientoId = +params['alojamientoId'];
          this.otroUsuarioId = +params['usuarioId'];

          // Cargar datos del otro usuario
          this.usuarioService.getUsuarioById(this.otroUsuarioId).subscribe(
            usuario => {
              this.otroUsuario = usuario;
            },
            error => {
              console.error('Error al cargar usuario:', error);
            }
          );

          // Cargar datos del alojamiento
          this.alojamientoService.getAlojamientoById(this.alojamientoId).subscribe(
            alojamiento => {
              this.alojamiento = alojamiento;
            },
            error => {
              console.error('Error al cargar alojamiento:', error);
            }
          );

          // Cargar conversación
          this.chatService.cargarConversacion(this.otroUsuarioId, this.alojamientoId);
        } else {
          // Si no hay parámetros, cargar la lista de conversaciones
          this.cargando = false;
        }
      })
    );
  }

  ngOnDestroy(): void {
    // Desuscribirse de todas las suscripciones
    this.subscriptions.forEach(sub => sub.unsubscribe());

    // Desconectar del WebSocket
    this.chatService.desconectar();
  }

  enviarMensaje(): void {
    if (!this.nuevoMensaje.trim() || !this.alojamientoId || !this.otroUsuarioId) {
      return;
    }

    this.chatService.enviarMensaje({
      receptorId: this.otroUsuarioId,
      alojamientoId: this.alojamientoId,
      contenido: this.nuevoMensaje
    });

    this.nuevoMensaje = '';
  }

  esMensajePropio(mensaje: MensajeResponse): boolean {
    return mensaje.emisorId === this.usuarioActual.id;
  }

  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleString();
  }
}
