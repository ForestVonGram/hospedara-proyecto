import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ComentarioResponse {
  id: number;
  usuarioId: number;
  usuarioNombre?: string;
  alojamientoId: number;
  alojamientoNombre?: string;
  texto: string;
  calificacion: number; // 1..5
}

export interface ComentarioCreateRequest {
  usuarioId: number;
  alojamientoId: number;
  texto: string;
  calificacion: number; // 1..5
}

@Injectable({ providedIn: 'root' })
export class ComentarioService {
  private baseUrl = 'http://localhost:8080/comentarios';

  constructor(private http: HttpClient) {}

  porAlojamiento(alojamientoId: number): Observable<ComentarioResponse[]> {
    return this.http.get<ComentarioResponse[]>(`${this.baseUrl}/alojamiento/${alojamientoId}`);
  }

  crear(payload: ComentarioCreateRequest): Observable<ComentarioResponse> {
    return this.http.post<ComentarioResponse>(`${this.baseUrl}`, payload);
  }

  porAnfitrion(anfitrionId: number): Observable<ComentarioResponse[]> {
    return this.http.get<ComentarioResponse[]>(`${this.baseUrl}/anfitrion/${anfitrionId}`);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
