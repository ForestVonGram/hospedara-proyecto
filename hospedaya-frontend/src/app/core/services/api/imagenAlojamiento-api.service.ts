// Frontend service for ImagenAlojamiento endpoints matching backend ImagenAlojamientoController
import { API_BASE_URL } from '../../../config/api';
const BASE_URL = API_BASE_URL;
const RESOURCE = '/imagenes-alojamiento';

export interface ImagenAlojamientoRequestDTO {
  alojamientoId: number;
  url: string;
}

export interface ImagenAlojamientoResponseDTO {
  id: number;
  alojamientoId: number;
  url: string;
}

async function http<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  const res = await fetch(input, {
    headers: { 'Content-Type': 'application/json', ...(init?.headers || {}) },
    ...init,
  });
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(text || `HTTP ${res.status}`);
  }
  if (res.status === 204) return undefined as unknown as T;
  return (await res.json()) as T;
}

export class ImagenAlojamientoApiService {
  static async getByAlojamiento(alojamientoId: number): Promise<ImagenAlojamientoResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}/alojamiento/${alojamientoId}`);
  }

  static async create(payload: ImagenAlojamientoRequestDTO): Promise<ImagenAlojamientoResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  static async delete(id: number): Promise<void> {
    const res = await fetch(`${BASE_URL}${RESOURCE}/${id}`, { method: 'DELETE' });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(text || `HTTP ${res.status}`);
    }
  }
}
