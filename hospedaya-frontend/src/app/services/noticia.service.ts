import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Noticia {
  id: number;
  titulo: string;
  resumen: string;
  contenido: string;
  fechaCreacion: string; // ISO string
}

@Injectable({ providedIn: 'root' })
export class NoticiaService {
  // Ajusta las URLs a los endpoints reales del backend cuando los tengas definidos
  private adminBaseUrl = 'http://localhost:8080/admin/noticias';
  private publicBaseUrl = 'http://localhost:8080/noticias';

  constructor(private http: HttpClient) {}

  // Listado completo para panel admin
  listarTodas(): Observable<Noticia[]> {
    return this.http.get<Noticia[]>(this.adminBaseUrl);
  }

  // Obtener una noticia específica (admin)
  obtener(id: number): Observable<Noticia> {
    return this.http.get<Noticia>(`${this.adminBaseUrl}/${id}`);
  }

  // Listado público para página de prensa
  listarPublicas(): Observable<Noticia[]> {
    return this.http.get<Noticia[]>(this.publicBaseUrl);
  }

  crear(data: Omit<Noticia, 'id' | 'fechaCreacion'>): Observable<Noticia> {
    return this.http.post<Noticia>(this.adminBaseUrl, data);
  }

  // Actualizar noticia existente (admin)
  actualizar(id: number, data: Omit<Noticia, 'id' | 'fechaCreacion'>): Observable<Noticia> {
    return this.http.put<Noticia>(`${this.adminBaseUrl}/${id}`, data);
  }

  // Eliminar noticia (admin)
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminBaseUrl}/${id}`);
  }
}
