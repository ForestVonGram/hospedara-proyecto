import type { Rol } from './UsuarioRequest';

export interface UsuarioUpdate {
  nombre?: string;
  telefono?: string;
  rol?: Rol;
}
