// Frontend service for Alojamiento-Servicio relation endpoints matching backend AlojamientoServicioController
import { API_BASE_URL } from '../config/api';
const BASE_URL = API_BASE_URL;
const RESOURCE = '/alojamiento-servicios';

export interface AlojamientoServicioRequestDTO {
  alojamientoId: number;
  servicioId: number;
}

export interface AlojamientoServicioResponseDTO {
  id: number;
  alojamientoId: number;
  servicioId: number;
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

export class AlojamientoServicioService {
  static async list(alojamientoId?: number): Promise<AlojamientoServicioResponseDTO[]> {
    const url = new URL(`${BASE_URL}${RESOURCE}`);
    if (typeof alojamientoId === 'number') {
      url.searchParams.set('alojamientoId', String(alojamientoId));
    }
    return http(url.toString());
  }

  static async create(payload: AlojamientoServicioRequestDTO): Promise<AlojamientoServicioResponseDTO> {
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
