import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface AlojamientoCreateRequest {
  nombre: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  maxHuespedes?: number;
  anfitrionId: number;
  latitud?: number;
  longitud?: number;
}

// DTO tal como lo entrega el backend
export interface AlojamientoResponseDTO {
  id: number;
  titulo: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  maxHuespedes?: number;
  anfitrionId?: number;
  latitud?: number;
  longitud?: number;
  // El backend podría no devolver imágenes aquí, pero lo mantenemos por compatibilidad si existiera
  imagenes?: string[];
  servicios?: string[];
}

export interface AlojamientoUpdateRequest {
  nombre?: string;
  descripcion?: string;
  direccion?: string;
  precioPorNoche?: number;
  maxHuespedes?: number;
  latitud?: number;
  longitud?: number;
}

// Modelo de dominio usado en la UI
export interface Alojamiento {
  id: number;
  titulo: string; // mapea desde nombre
  descripcion: string;
  direccion: string;
  precioPorNoche: number | string; // algunas vistas lo tratan como string
  anfitrionId?: number;
  latitud?: number;
  longitud?: number;
  imagenes?: string[];
  servicios?: string[];
}

function coerceNumber(value: any): number | undefined {
  if (typeof value === 'number') return Number.isFinite(value) ? value : undefined;
  if (typeof value === 'string' && value.trim() !== '') {
    const n = Number(value);
    return Number.isFinite(n) ? n : undefined;
  }
  return undefined;
}

function dtoToAlojamiento(dto: AlojamientoResponseDTO): Alojamiento {
  // Aceptar múltiples alias de coordenadas desde el backend
  const anyDto = dto as any;
  const latRaw = anyDto.latitud ?? anyDto.latitude ?? anyDto.lat;
  const lngRaw = anyDto.longitud ?? anyDto.longitude ?? anyDto.lng ?? anyDto.lon;

  const latNum = coerceNumber(latRaw);
  const lngNum = coerceNumber(lngRaw);

  return {
    id: dto.id,
    titulo: dto.titulo,
    descripcion: dto.descripcion,
    direccion: dto.direccion,
    precioPorNoche: dto.precioPorNoche,
    anfitrionId: dto.anfitrionId,
    latitud: latNum,
    longitud: lngNum,
    imagenes: dto.imagenes || [],
    servicios: Array.isArray(dto.servicios) ? dto.servicios : []
  };
}

@Injectable({ providedIn: 'root' })
export class AlojamientoService {
  private baseUrl = 'http://localhost:8080/alojamientos';

  constructor(private http: HttpClient) {}

  // Crear (se mantiene con DTO del backend)
  crearAlojamiento(req: AlojamientoCreateRequest): Observable<AlojamientoResponseDTO> {
    return this.http.post<AlojamientoResponseDTO>(`${this.baseUrl}`, req);
  }

  // Actualizar parcial o total (PUT en backend actual)
  actualizarAlojamiento(id: number, req: AlojamientoUpdateRequest): Observable<AlojamientoResponseDTO> {
    return this.http.put<AlojamientoResponseDTO>(`${this.baseUrl}/${id}`, req);
  }

  // Eliminar por id
  eliminarAlojamiento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Listado general para página de resultados (mapea a modelo de UI)
  listar(): Observable<Alojamiento[]> {
    return this.http
      .get<AlojamientoResponseDTO[]>(`${this.baseUrl}`)
      .pipe(map(list => (list || []).map(dtoToAlojamiento)));
  }

  // Listado por anfitrión (se mantiene como DTO para pantallas de gestión)
  listarPorAnfitrion(anfitrionId: number): Observable<AlojamientoResponseDTO[]> {
    return this.http.get<AlojamientoResponseDTO[]>(`${this.baseUrl}/anfitrion/${anfitrionId}`);
  }

  // Obtener por id en formato DTO
  obtenerPorId(id: number): Observable<AlojamientoResponseDTO> {
    return this.http.get<AlojamientoResponseDTO>(`${this.baseUrl}/${id}`);
  }

  // Obtener por id para la UI (mapea DTO -> UI)
  obtener(id: number): Observable<Alojamiento> {
    return this.obtenerPorId(id).pipe(map(dtoToAlojamiento));
  }
}
