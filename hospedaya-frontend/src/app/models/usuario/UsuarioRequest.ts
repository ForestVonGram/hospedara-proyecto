export type Rol = 'USUARIO' | 'ANFITRION' | 'ADMIN';

export interface UsuarioRequest {
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
  rol?: Rol;
}
