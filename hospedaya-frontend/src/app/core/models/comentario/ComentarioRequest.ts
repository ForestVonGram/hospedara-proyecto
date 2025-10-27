export interface ComentarioRequest {
  usuarioId: number;
  alojamientoId: number;
  texto: string;
  calificacion: number; // 0-5
}
