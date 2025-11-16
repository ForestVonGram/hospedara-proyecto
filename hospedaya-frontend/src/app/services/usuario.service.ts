import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

export interface UsuarioAdminSummary {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  fotoPerfilUrl?: string;
  rol?: string;
  fechaRegistro?: string;
  activo?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private baseUrl = 'http://localhost:8080/usuarios';
  private adminBaseUrl = 'http://localhost:8080/admin/usuarios';

  constructor(private http: HttpClient) {}

  me(): Observable<UsuarioProfile> {
    return this.http.get<UsuarioProfile>(`${this.baseUrl}/me`);

  }

  // Obtener un usuario por id (uso administrativo)
  obtener(id: number): Observable<UsuarioProfile> {
    return this.http.get<any>(`${this.baseUrl}/${id}`).pipe(
      map(u => ({
        id: u.id,
        nombre: u.nombre,
        email: u.email,
        telefono: u.telefono,
        fotoPerfilUrl: u.fotoPerfilUrl,
        // Normalizar rol para que siempre sea un string plano (HUESPED/ANFITRION/ADMIN)
        rol: typeof u.rol === 'string' ? u.rol : (u.rol?.name ?? u.rol),
        fechaRegistro: u.fechaRegistro,
        activo: u.activo,
      }) as UsuarioProfile)
    );
  }

  // Listar todos los usuarios (uso administrativo)
  listarTodos(): Observable<UsuarioAdminSummary[]> {
    return this.http.get<any[]>(`${this.baseUrl}`).pipe(
      map(list => (list || []).map(u => ({
        id: u.id,
        nombre: u.nombre,
        email: u.email,
        telefono: u.telefono,
        fotoPerfilUrl: u.fotoPerfilUrl,
        rol: typeof u.rol === 'string' ? u.rol : (u.rol?.name ?? u.rol),
        fechaRegistro: u.fechaRegistro,
        activo: u.activo,
      }) as UsuarioAdminSummary))
    );
  }

  update(id: number, data: Partial<UsuarioProfile>): Observable<UsuarioProfile> {
    return this.http.put<UsuarioProfile>(`${this.baseUrl}/${id}`, data);
  }

  // Eliminar usuario (uso administrativo)
  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }

  // Eliminar cuenta propia del usuario autenticado (requiere contraseña)
  eliminarCuentaPropia(password: string): Observable<string> {
    return this.http.delete(`${this.baseUrl}/me`, {
      body: { password },
      responseType: 'text',
    }) as Observable<string>;
  }

  // Activar usuario (admin)
  activar(id: number): Observable<UsuarioProfile> {
    return this.http.patch<UsuarioProfile>(`${this.adminBaseUrl}/${id}/activar`, {});
  }

  // Desactivar / bloquear usuario (admin)
  desactivar(id: number): Observable<UsuarioProfile> {
    return this.http.patch<UsuarioProfile>(`${this.adminBaseUrl}/${id}/desactivar`, {});
  }

  uploadFoto(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.baseUrl}/${id}/foto`, formData, { responseType: 'text' });
  }
}
