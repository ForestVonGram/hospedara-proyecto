// Frontend service for Alojamiento-Servicio relation endpoints matching backend AlojamientoServicioController
import { API_BASE_URL } from '../../../config/api';
import { AlojamientoServicioRequest } from '../../models/alojamientoservicio/AlojamientoServicioRequest';
import { AlojamientoServicioResponse } from '../../models/alojamientoservicio/AlojamientoServicioResponse';

const BASE_URL = API_BASE_URL;
const RESOURCE = '/alojamiento-servicios';

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

export class AlojamientoServicioApiService {
  static async list(alojamientoId?: number): Promise<AlojamientoServicioResponse[]> {
    const url = new URL(`${BASE_URL}${RESOURCE}`);
    if (typeof alojamientoId === 'number') {
      url.searchParams.set('alojamientoId', String(alojamientoId));
    }
    return http(url.toString());
  }

  static async create(payload: AlojamientoServicioRequest): Promise<AlojamientoServicioResponse> {
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
