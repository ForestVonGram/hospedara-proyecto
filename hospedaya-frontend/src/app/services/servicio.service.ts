import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ServicioDTO {
  id: number;
  nombre: string;
  descripcion?: string;
}

export interface ServicioCreateRequest {
  nombre: string;
  descripcion?: string;
}

@Injectable({ providedIn: 'root' })
export class ServicioService {
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/servicios';

  constructor(private http: HttpClient) {}

  listar(): Observable<ServicioDTO[]> {
    return this.http.get<ServicioDTO[]>(`${this.baseUrl}`);
  }

  crear(payload: ServicioCreateRequest): Observable<ServicioDTO> {
    return this.http.post<ServicioDTO>(`${this.baseUrl}`, payload);
  }
}
