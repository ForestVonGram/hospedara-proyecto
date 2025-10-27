export interface TransaccionPagoResponse {
  id: number;
  pagoId: number;
  proveedor: string;
  referencia?: string;
  estado: string;
  fechaCreacion: string; // ISO date-time
}
