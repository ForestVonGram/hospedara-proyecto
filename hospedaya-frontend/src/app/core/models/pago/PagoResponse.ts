export interface PagoResponse {
  id: number;
  reservaId: number;
  monto: number;
  estado: string;
  referenciaExterna?: string;
  fechaCreacion: string; // ISO date-time
  fechaConfirmacion?: string; // ISO date-time
}
