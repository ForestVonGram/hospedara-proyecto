export interface TransaccionPagoRequest {
  pagoId: number;
  proveedor: string; // e.g., STRIPE, PAYPAL
  referencia?: string;
}
