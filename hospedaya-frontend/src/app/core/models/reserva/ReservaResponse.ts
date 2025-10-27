export interface ReservaResponse {
  id: number;
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string; // yyyy-MM-dd
  estado: string;
  fechaCreacion: string; // ISO date-time
}
