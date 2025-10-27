export type TipoNotificacion = 'RESERVA' | 'PAGO' | 'COMENTARIO' | 'SISTEMA';

export interface NotificacionRequest {
  usuarioId: number;
  mensaje: string;
  tipo: TipoNotificacion;
}
