import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ImagenAlojamientoCreateRequest {
  alojamientoId: number;
  url: string;
}

export interface ImagenAlojamientoResponseDTO {
  id: number;
  alojamientoId: number;
  url: string;
}

@Injectable({ providedIn: 'root' })
export class ImagenAlojamientoService {
  private baseUrl = 'http://localhost:8080/imagenes-alojamiento';

  constructor(private http: HttpClient) {}

  listarPorAlojamiento(alojamientoId: number): Observable<ImagenAlojamientoResponseDTO[]> {
    return this.http.get<ImagenAlojamientoResponseDTO[]>(`${this.baseUrl}/alojamiento/${alojamientoId}`);
    }

  agregarImagen(req: ImagenAlojamientoCreateRequest): Observable<ImagenAlojamientoResponseDTO> {
    return this.http.post<ImagenAlojamientoResponseDTO>(`${this.baseUrl}`, req);
  }

  eliminarImagen(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
