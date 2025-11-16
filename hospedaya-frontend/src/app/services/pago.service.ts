import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PagoRequest {
  reservaId: number;
  monto: number; // en la API es BigDecimal, aquí enviamos number
  referenciaExterna?: string;
}

export interface PagoResponseDTO {
  id: number;
  reservaId: number;
  monto: number;
  estado?: string;
  referenciaExterna?: string;
  fechaCreacion?: string;
  fechaConfirmacion?: string;
}

@Injectable({ providedIn: 'root' })
export class PagoService {
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/pagos';

  constructor(private http: HttpClient) {}

  registrarPago(req: PagoRequest): Observable<PagoResponseDTO> {
    return this.http.post<PagoResponseDTO>(`${this.baseUrl}`, req);
  }

  iniciarPago(pagoId: number): Observable<{ init_point: string }> {
    return this.http.post<{ init_point: string }>(`${this.baseUrl}/${pagoId}/iniciar`, {});
  }
}
