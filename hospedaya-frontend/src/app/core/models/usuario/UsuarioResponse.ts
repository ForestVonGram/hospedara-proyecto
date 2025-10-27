export type Rol = 'USUARIO' | 'ANFITRION' | 'ADMIN';

export interface UsuarioResponse {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  rol: Rol;
  fechaRegistro: string; // ISO date
  activo: boolean;
}
