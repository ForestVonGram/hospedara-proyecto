// Frontend service for Comentario endpoints matching backend ComentarioController
const BASE_URL = 'http://localhost:8080';
const RESOURCE = '/comentarios';

export interface ComentarioRequestDTO {
  usuarioId: number;
  alojamientoId: number;
  texto: string;
  calificacion: number;
}

export interface ComentarioResponseDTO {
  id: number;
  usuarioId: number;
  alojamientoId: number;
  texto: string;
  calificacion: number;
  fechaCreacion?: string;
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

export class ComentarioService {
  static async getByAlojamiento(alojamientoId: number): Promise<ComentarioResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}/alojamiento/${alojamientoId}`);
  }

  static async getById(id: number): Promise<ComentarioResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}/${id}`);
  }

  static async create(payload: ComentarioRequestDTO): Promise<ComentarioResponseDTO> {
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
