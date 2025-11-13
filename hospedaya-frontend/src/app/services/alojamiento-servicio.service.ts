import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AlojamientoServicioDTO {
  relacionId: number;
  alojamientoId: number;
  servicioId: number;
  nombre?: string;
  descripcion?: string;
}

export interface AsignarServicioRequest {
  alojamientoId: number;
  servicioId: number;
}

@Injectable({ providedIn: 'root' })
export class AlojamientoServicioService {
  private baseUrl = 'http://localhost:8080/alojamiento-servicios';

  constructor(private http: HttpClient) {}

  listarPorAlojamiento(alojamientoId: number): Observable<AlojamientoServicioDTO[]> {
    return this.http.get<AlojamientoServicioDTO[]>(`${this.baseUrl}`, { params: { alojamientoId } as any });
  }

  asignar(payload: AsignarServicioRequest): Observable<AlojamientoServicioDTO> {
    return this.http.post<AlojamientoServicioDTO>(`${this.baseUrl}`, payload);
  }

  eliminarRelacion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
