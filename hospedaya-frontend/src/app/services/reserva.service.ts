import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReservaRequest {
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;    // yyyy-MM-dd
}

export interface ReservaResponseDTO {
  id: number;
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string;
  fechaFin: string;
  estado?: string;
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private baseUrl = 'http://localhost:8080/reservas';

  constructor(private http: HttpClient) {}

  crearReserva(req: ReservaRequest): Observable<ReservaResponseDTO> {
    return this.http.post<ReservaResponseDTO>(`${this.baseUrl}`, req);
  }
}
