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
  private baseUrl = 'http://localhost:8080/reservas';

  constructor(private http: HttpClient) {}

  crearReserva(req: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(`${this.baseUrl}`, req);
  }

  // Listar reservas por usuario (ruta común, ajusta si tu backend usa otra)
  porUsuario(usuarioId: number): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.baseUrl}/usuario/${usuarioId}`);
  }
}
