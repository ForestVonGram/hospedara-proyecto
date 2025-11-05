import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Reserva {
  id: number;
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string; // ISO yyyy-MM-dd
  fechaFin: string;    // ISO yyyy-MM-dd
  estado?: string;
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private baseUrl = 'http://localhost:8080/reservas';

  constructor(private http: HttpClient) {}

  porUsuario(usuarioId: number): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(`${this.baseUrl}/usuario/${usuarioId}`);
  }
}