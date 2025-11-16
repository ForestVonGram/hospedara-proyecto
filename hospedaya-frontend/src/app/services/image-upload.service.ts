import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpHeaders, HttpRequest } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface ImageUploadResult {
  url: string;
  publicId: string;
  format?: string;
  bytes?: number;
  width?: number;
  height?: number;
}

@Injectable({ providedIn: 'root' })
export class ImageUploadService {
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/imagenes';

  constructor(private http: HttpClient) {}

  uploadImage(file: File, carpeta: 'alojamientos' | 'perfiles' | string = 'alojamientos'): Observable<ImageUploadResult> {
    const form = new FormData();
    form.append('file', file);

    return this.http.post<ImageUploadResult>(`${this.baseUrl}/upload?carpeta=${encodeURIComponent(carpeta)}`, form);
  }

  // Ejemplo con progreso si se requiere en el futuro
  uploadWithProgress(file: File, carpeta: string = 'alojamientos'): Observable<{ progress: number; result?: ImageUploadResult }>
  {
    const form = new FormData();
    form.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/upload?carpeta=${encodeURIComponent(carpeta)}`, form, {
      reportProgress: true
    });

    return this.http.request(req).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            const progress = event.total ? Math.round((100 * event.loaded) / event.total) : 0;
            return { progress };
          case HttpEventType.Response:
            return { progress: 100, result: event.body as ImageUploadResult };
          default:
            return { progress: 0 };
        }
      })
    );
  }

  deleteByPublicId(publicId: string) {
    return this.http.delete<void>(`${this.baseUrl}/${encodeURIComponent(publicId)}`);
  }
}
