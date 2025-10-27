export interface AlojamientoResponse {
  id: number;
  titulo: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  anfitrionId: number;
  imagenes: string[];
  servicios: string[];
}
