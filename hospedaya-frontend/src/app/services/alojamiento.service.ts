import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AlojamientoCreateRequest {
  nombre: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  anfitrionId: number;
}

export interface AlojamientoResponseDTO {
  id: number;
  nombre: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  anfitrionId?: number;
}

@Injectable({ providedIn: 'root' })
export class AlojamientoService {
  private baseUrl = 'http://localhost:8080/alojamientos';

  constructor(private http: HttpClient) {}

  crearAlojamiento(req: AlojamientoCreateRequest): Observable<AlojamientoResponseDTO> {
    return this.http.post<AlojamientoResponseDTO>(`${this.baseUrl}`, req);
  }

  listarPorAnfitrion(anfitrionId: number): Observable<AlojamientoResponseDTO[]> {
    return this.http.get<AlojamientoResponseDTO[]>(`${this.baseUrl}/anfitrion/${anfitrionId}`);
  }

  obtenerPorId(id: number): Observable<AlojamientoResponseDTO> {
    return this.http.get<AlojamientoResponseDTO>(`${this.baseUrl}/${id}`);
  }
}
