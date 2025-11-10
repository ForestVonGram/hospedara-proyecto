import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UsuarioProfile {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  fotoPerfilUrl?: string;
  rol: string;
  activo?: boolean;
  fechaRegistro?: string;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private baseUrl = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient) {}

  me(): Observable<UsuarioProfile> {
    return this.http.get<UsuarioProfile>(`${this.baseUrl}/me`);
    
  }

  update(id: number, data: Partial<UsuarioProfile>): Observable<UsuarioProfile> {
    return this.http.put<UsuarioProfile>(`${this.baseUrl}/${id}`, data);
  }

  uploadFoto(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/${id}/foto`, formData, { responseType: 'text' });
  }
}
