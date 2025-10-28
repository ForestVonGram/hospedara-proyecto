export interface AlojamientoServicioResponse {
  // Relation data
  relacionId: number; // id de la relación alojamiento-servicio
  alojamientoId: number;
  servicioId: number;
  detalle?: string;

  // Embedded service data (compatibilidad con backend)
  id: number; // id del servicio
  nombre: string;
  descripcion?: string;
}
