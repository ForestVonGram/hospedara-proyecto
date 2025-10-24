// Frontend service for Servicio endpoints matching backend ServicioController
import { API_BASE_URL } from '../config/api';
const BASE_URL = API_BASE_URL;
const RESOURCE = '/servicios';

export interface ServicioRequestDTO {
  nombre: string;
  descripcion?: string;
}

export interface ServicioResponseDTO {
  id: number;
  nombre: string;
  descripcion?: string;
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

export class ServicioService {
  static async getAll(): Promise<ServicioResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}`);
  }

  static async getById(id: number): Promise<ServicioResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}/${id}`);
  }

  static async create(payload: ServicioRequestDTO): Promise<ServicioResponseDTO> {
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
