// Frontend service for Alojamiento endpoints matching backend AlojamientoController
// Minimal dependency version using fetch API

const BASE_URL = 'http://localhost:8080';
const RESOURCE = '/alojamientos';

export interface AlojamientoRequestDTO {
  nombre: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  anfitrionId: number;
}

export interface AlojamientoUpdateDTO {
  nombre?: string;
  descripcion?: string;
  direccion?: string;
  precioPorNoche?: number;
}

export interface AlojamientoResponseDTO {
  id: number;
  nombre: string;
  descripcion: string;
  direccion: string;
  precioPorNoche: number;
  anfitrionId: number;
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

export class AlojamientoService {
  static async getAll(): Promise<AlojamientoResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}`);
  }

  static async getByAnfitrion(anfitrionId: number): Promise<AlojamientoResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}/anfitrion/${anfitrionId}`);
  }

  static async getById(id: number): Promise<AlojamientoResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}/${id}`);
  }

  static async create(payload: AlojamientoRequestDTO): Promise<AlojamientoResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  static async update(id: number, payload: AlojamientoUpdateDTO): Promise<AlojamientoResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  }

  static async delete(id: number): Promise<string> {
    // Backend returns text message on success
    const res = await fetch(`${BASE_URL}${RESOURCE}/${id}`, { method: 'DELETE' });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(text || `HTTP ${res.status}`);
    }
    return res.text();
  }
}
