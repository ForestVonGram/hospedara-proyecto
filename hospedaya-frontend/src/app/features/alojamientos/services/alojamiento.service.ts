import { Injectable } from '@angular/core';
import { from, map, Observable } from 'rxjs';
import {
  AlojamientoApiService,
  AlojamientoResponseDTO,
  AlojamientoRequestDTO,
  AlojamientoUpdateDTO,
} from '../../../core/services/api/alojamiento-api.service';
import {
  ImagenAlojamientoApiService,
  ImagenAlojamientoResponseDTO,
} from '../../../core/services/api/imagenAlojamiento-api.service';

@Injectable({ providedIn: 'root' })
export class AlojamientoService {
  // Alojamiento CRUD
  getAll(): Observable<AlojamientoResponseDTO[]> {
    return from(AlojamientoApiService.getAll());
  }

  getById(id: number): Observable<AlojamientoResponseDTO> {
    return from(AlojamientoApiService.getById(id));
  }

  getByAnfitrion(anfitrionId: number): Observable<AlojamientoResponseDTO[]> {
    return from(AlojamientoApiService.getByAnfitrion(anfitrionId));
  }

  create(payload: AlojamientoRequestDTO): Observable<AlojamientoResponseDTO> {
    return from(AlojamientoApiService.create(payload));
  }

  update(id: number, payload: AlojamientoUpdateDTO): Observable<AlojamientoResponseDTO> {
    return from(AlojamientoApiService.update(id, payload));
  }

  delete(id: number): Observable<string> {
    return from(AlojamientoApiService.delete(id));
  }

  // Images
  getImagesByAlojamiento(alojamientoId: number): Observable<ImagenAlojamientoResponseDTO[]> {
    return from(ImagenAlojamientoApiService.getByAlojamiento(alojamientoId));
  }

  getCoverImageUrl$(alojamientoId: number): Observable<string | null> {
    return this.getImagesByAlojamiento(alojamientoId).pipe(
      map((imgs) => (imgs && imgs.length > 0 ? imgs[0].url : null))
    );
  }
}
