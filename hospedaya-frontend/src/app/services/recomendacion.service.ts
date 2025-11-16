import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AlojamientoResponseDTO } from './alojamiento.service';

@Injectable({ providedIn: 'root' })
export class RecomendacionService {
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/recomendaciones';

  constructor(private http: HttpClient) {}

  porUsuario(usuarioId: number, limit = 8): Observable<AlojamientoResponseDTO[]> {
    return this.http.get<AlojamientoResponseDTO[]>(`${this.baseUrl}/usuario/${usuarioId}?limit=${limit}`);
  }
}
