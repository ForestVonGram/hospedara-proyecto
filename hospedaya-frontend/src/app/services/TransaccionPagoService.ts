// Frontend service for TransaccionPago endpoints matching backend TransaccionPagoController
const BASE_URL = 'http://localhost:8080';
const RESOURCE = '/transacciones-pago';

export interface TransaccionPagoRequestDTO {
  pagoId: number;
  monto: number;
  referenciaExterna?: string;
  detalle?: string;
}

export interface TransaccionPagoResponseDTO {
  id: number;
  pagoId: number;
  monto: number;
  referenciaExterna?: string;
  detalle?: string;
  fecha?: string;
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

export class TransaccionPagoService {
  static async getAll(): Promise<TransaccionPagoResponseDTO[]> {
    return http(`${BASE_URL}${RESOURCE}`);
  }

  static async getById(id: number): Promise<TransaccionPagoResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}/${id}`);
  }

  static async create(payload: TransaccionPagoRequestDTO): Promise<TransaccionPagoResponseDTO> {
    return http(`${BASE_URL}${RESOURCE}`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  }
}
