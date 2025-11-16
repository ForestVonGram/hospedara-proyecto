import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReservaRequest {
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;    // yyyy-MM-dd
}

// DTO del backend
export interface ReservaResponseDTO {
  id: number;
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string;
  fechaFin: string;
  estado?: string;
}

// Modelo usado en la UI (igual al DTO por ahora)
export interface Reserva extends ReservaResponseDTO {}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/reservas';

  constructor(private http: HttpClient) {}

  crearReserva(req: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.baseUrl}`, req);
  }

  // Listar reservas por usuario (ruta común, ajusta si tu backend usa otra)
  porUsuario(usuarioId: number): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.baseUrl}/usuario/${usuarioId}`);
  }

  // Listar todas las reservas (para que el anfitrión filtre por sus alojamientos)
  listarTodas(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.baseUrl}`);
  }

  // Obtener una reserva por id
  obtener(id: number): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.baseUrl}/${id}`);
  }

  // Cancelar una reserva
  cancelar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
