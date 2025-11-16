import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

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
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/comentarios';

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<ComentarioResponse[]> {
    return this.http.get<ComentarioResponse[]>(`${this.baseUrl}`);
  }

  porAlojamiento(alojamientoId: number): Observable<ComentarioResponse[]> {
    return this.http
      .get<ComentarioResponse[]>(`${this.baseUrl}/alojamiento/${alojamientoId}`)
      .pipe(
        catchError(err => {
          // Si no hay comentarios, el backend puede devolver 404; lo tratamos como lista vacía
          if (err.status === 404) {
            return of([] as ComentarioResponse[]);
          }
          throw err;
        })
      );
  }

  crear(payload: ComentarioCreateRequest): Observable<ComentarioResponse> {
    return this.http.post<ComentarioResponse>(`${this.baseUrl}`, payload);
  }

  porAnfitrion(anfitrionId: number): Observable<ComentarioResponse[]> {
    return this.http
      .get<ComentarioResponse[]>(`${this.baseUrl}/anfitrion/${anfitrionId}`)
      .pipe(
        catchError(err => {
          if (err.status === 404) {
            return of([] as ComentarioResponse[]);
          }
          throw err;
        })
      );
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
