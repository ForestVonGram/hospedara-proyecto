// Frontend service for Reserva endpoints matching backend ReservaController
import { API_BASE_URL } from '../../../config/api';
const BASE_URL = API_BASE_URL;
const RESOURCE = '/reservas';

export interface ReservaRequestDTO {
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string; // ISO date string
  fechaFin: string; // ISO date string
}

export interface ReservaResponseDTO {
  id: number;
  usuarioId: number;
  alojamientoId: number;
  fechaInicio: string;
  fechaFin: string;
  estado?: string;
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

export class ReservaApiService {
  static async getAll(): Promise<ReservaResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}`);
  }

  static async getByUsuario(usuarioId: number): Promise<ReservaResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}/usuario/${usuarioId}`);
  }

  static async getById(reservaId: number): Promise<ReservaResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}/${reservaId}`);
  }

  static async create(payload: ReservaRequestDTO): Promise<ReservaResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }

  static async cancel(id: number): Promise<void> {
    const res = await fetch(`${BASE_URL}${RESOURCE}/${id}`, { method: 'DELETE' });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(text || `HTTP ${res.status}`);
    }
  }
}
