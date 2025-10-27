import type { TipoNotificacion } from './NotificacionRequest';

export interface NotificacionResponse {
  id: number;
  usuarioId: number;
  mensaje: string;
  tipo: TipoNotificacion;
  leida: boolean;
  fechaCreacion: string; // ISO date-time
}
