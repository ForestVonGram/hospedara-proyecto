export interface ReservaRequest {
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string; // yyyy-MM-dd
}
