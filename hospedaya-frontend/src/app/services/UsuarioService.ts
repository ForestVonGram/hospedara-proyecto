// Frontend service for Usuario endpoints matching backend UsuarioController
const BASE_URL = 'http://localhost:8080';
const RESOURCE = '/usuarios';

export interface Usuario {
  id?: number;
  nombre: string;
  email: string;
  password: string;
  rol?: string;
  fechaRegistro?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
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

export class UsuarioService {
  static async getAll(): Promise<Usuario[]> {
    return http(`${BASE_URL}${RESOURCE}`);
  }

  static async create(usuario: Usuario): Promise<Usuario> {
    return http(`${BASE_URL}${RESOURCE}`, {
      method: 'POST',
      body: JSON.stringify(usuario),
    });
  }

  static async login(request: LoginRequest): Promise<string> {
    // Backend returns a success message string
    const res = await fetch(`${BASE_URL}${RESOURCE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    const text = await res.text().catch(() => '');
    if (!res.ok) {
      throw new Error(text || `HTTP ${res.status}`);
    }
    return text;
  }

  static async update(id: number, usuario: Usuario): Promise<Usuario> {
    return http(`${BASE_URL}${RESOURCE}/${id}`, {
      method: 'PUT',
      body: JSON.stringify(usuario),
    });
  }

  static async delete(id: number): Promise<string> {
    const res = await fetch(`${BASE_URL}${RESOURCE}/${id}`, { method: 'DELETE' });
    const text = await res.text().catch(() => '');
    if (!res.ok) {
      throw new Error(text || `HTTP ${res.status}`);
    }
    return text;
  }
}
