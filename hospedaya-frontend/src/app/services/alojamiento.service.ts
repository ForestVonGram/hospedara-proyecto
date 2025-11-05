import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Alojamiento {
  id: number;
  titulo: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  anfitrionId: number;
  imagenes?: string[];
  servicios?: string[];
}

@Injectable({ providedIn: 'root' })
export class AlojamientoService {
  private baseUrl = 'http://localhost:8080/alojamientos';

  constructor(private http: HttpClient) {}

  listar(): Observable<Alojamiento[]> {
    return this.http.get<Alojamiento[]>(this.baseUrl);
  }

  obtener(id: number): Observable<Alojamiento> {
    return this.http.get<Alojamiento>(`${this.baseUrl}/${id}`);
  }
}