import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ImageUploadResponse {
  url: string;
  message: string;
}

export interface MultipleImageUploadResponse {
  urls: string[];
  count: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ImagenService {
  private baseUrl = 'https://hospedaya-proyecto.onrender.com/imagenes';

  constructor(private http: HttpClient) {}

  /**
   * Sube una imagen de avatar
   */
  uploadAvatar(file: File): Observable<ImageUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImageUploadResponse>(`${this.baseUrl}/avatar`, formData);
  }

  /**
   * Sube una imagen de alojamiento
   */
  uploadAlojamientoImage(file: File): Observable<ImageUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImageUploadResponse>(`${this.baseUrl}/alojamiento`, formData);
  }

  /**
   * Sube múltiples imágenes de alojamiento
   */
  uploadMultipleAlojamientoImages(files: File[]): Observable<MultipleImageUploadResponse> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    return this.http.post<MultipleImageUploadResponse>(`${this.baseUrl}/alojamiento/multiple`, formData);
  }

  /**
   * Convierte un archivo a base64 para preview
   */
  fileToDataUrl(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }

  /**
   * Valida que el archivo sea una imagen
   */
  isValidImageFile(file: File): boolean {
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    return validTypes.includes(file.type);
  }

  /**
   * Valida el tamaño del archivo
   */
  isValidImageSize(file: File, maxSizeMB: number = 5): boolean {
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    return file.size <= maxSizeBytes;
  }
}
