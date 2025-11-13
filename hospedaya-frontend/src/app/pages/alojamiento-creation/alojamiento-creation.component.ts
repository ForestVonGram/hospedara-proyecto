import { Component, OnInit } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AlojamientoService, AlojamientoCreateRequest, AlojamientoResponseDTO, AlojamientoUpdateRequest } from '../../services/alojamiento.service';
import { ImagenAlojamientoService, ImagenAlojamientoCreateRequest, ImagenAlojamientoResponseDTO } from '../../services/imagen-alojamiento.service';
import { ImageUploadService, ImageUploadResult } from '../../services/image-upload.service';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { MapboxMapComponent } from '../../mapbox/mapbox-map.component';
import { MarkerComponent } from '../../mapbox/marker.component';

@Component({
  selector: 'app-alojamiento-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, FormsModule, HeaderComponent, MapboxMapComponent, MarkerComponent],
  templateUrl: './alojamiento-creation.component.html',
  styleUrl: './alojamiento-creation.component.css'
})
export class AlojamientoCreationComponent implements OnInit {
  form!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  isEdit = false;
  editId: number | null = null;

  // Mapa: selección de ubicación
  selectedLngLat: [number, number] | null = null;

  // Gestión de imágenes (URLs + id cuando existen en backend)
  nuevaImagenUrl = '';
  imagenesList: { id?: number; url: string; isNew?: boolean }[] = [];

  // Subida de archivos
  selectedFile: File | null = null;
  uploadProgress = 0;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private alojamientoService: AlojamientoService,
    private imagenService: ImagenAlojamientoService,
    private imageUpload: ImageUploadService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (!user) {
      // No autenticado
      this.router.navigate(['/login']);
      return;
    }
    if (!user.rol || user.rol !== 'ANFITRION') {
      // Solo anfitriones pueden gestionar alojamientos
      this.router.navigate(['/register-host']);
      return;
    }

    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(120)]],
      descripcion: ['', [Validators.required, Validators.maxLength(1000)]],
      direccion: ['', [Validators.required, Validators.maxLength(200)]],
      precioPorNoche: [null, [Validators.required, Validators.min(0)]],
      latitud: [null],
      longitud: [null]
    });

    // Detectar modo edición por query param ?editId=123
    this.route.queryParamMap.subscribe(params => {
      const idParam = params.get('editId');
      if (idParam) {
        const id = Number(idParam);
        if (!isNaN(id)) {
          this.isEdit = true;
          this.editId = id;
          // Cargar datos existentes
          this.alojamientoService.obtenerPorId(id).subscribe({
            next: (dto: AlojamientoResponseDTO) => {
              this.form.patchValue({
                nombre: dto.titulo,
                descripcion: dto.descripcion,
                direccion: dto.direccion,
                precioPorNoche: dto.precioPorNoche,
                latitud: (dto as any).latitud ?? null,
                longitud: (dto as any).longitud ?? null
              });
              if (typeof (dto as any).longitud === 'number' && typeof (dto as any).latitud === 'number') {
                this.selectedLngLat = [Number((dto as any).longitud), Number((dto as any).latitud)];
              }
              // Cargar imágenes desde servicio dedicado para obtener IDs
              this.imagenService.listarPorAlojamiento(id).subscribe({
                next: (imgs) => {
                  this.imagenesList = (imgs || []).map(i => ({ id: i.id, url: i.url }));
                },
                error: () => {
                  // Fallback: si falla, usa lo que venga en el DTO (sin IDs)
                  if (Array.isArray((dto as any).imagenes)) {
                    this.imagenesList = ((dto as any).imagenes as string[]).map(u => ({ url: u }));
                  }
                }
              });
            },
            error: (err) => {
              this.errorMessage = typeof err?.error === 'string' && err.error.trim().length > 0
                ? err.error
                : err?.error?.message || 'No se pudo cargar el alojamiento para editar.';
            }
          });
        }
      }
    });
  }

  get f() { return this.form.controls; }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.auth.getUser();
    if (!user || user.rol !== 'ANFITRION') {
      this.errorMessage = 'Solo los anfitriones pueden gestionar alojamientos.';
      return;
    }

    // Sincronizar coordenadas seleccionadas con el formulario, si aplica
    if (this.selectedLngLat) {
      const [lng, lat] = this.selectedLngLat;
      this.form.patchValue({ latitud: lat, longitud: lng });
    }

    this.isSubmitting = true;

    if (this.isEdit && this.editId != null) {
      const update: AlojamientoUpdateRequest = {
        nombre: this.f['nombre'].value,
        descripcion: this.f['descripcion'].value,
        direccion: this.f['direccion'].value,
        precioPorNoche: Number(this.f['precioPorNoche'].value),
        latitud: this.form.value.latitud ?? undefined,
        longitud: this.form.value.longitud ?? undefined
      };

      this.alojamientoService.actualizarAlojamiento(this.editId, update).subscribe({
        next: async (resp: AlojamientoResponseDTO) => {
          // Subir nuevas imágenes añadidas (si son URLs nuevas)
          await this.subirImagenesSiCorresponde(this.editId!);
          this.successMessage = 'Alojamiento actualizado correctamente';
          this.router.navigate(['/alojamientos/gestion']);
        },
        error: (err) => {
          if (typeof err?.error === 'string' && err.error.trim().length > 0) {
            this.errorMessage = err.error;
          } else if (err?.error?.message) {
            this.errorMessage = err.error.message;
          } else if (err.status === 400) {
            this.errorMessage = 'Datos inválidos. Verifica la información ingresada.';
          } else if (err.status === 404) {
            this.errorMessage = 'Alojamiento no encontrado para editar.';
          } else {
            this.errorMessage = 'Error al actualizar el alojamiento. Inténtalo nuevamente.';
          }
        },
        complete: () => {
          this.isSubmitting = false;
        }
      });
      return;
    }

    const payload: AlojamientoCreateRequest = {
      nombre: this.f['nombre'].value,
      descripcion: this.f['descripcion'].value,
      direccion: this.f['direccion'].value,
      precioPorNoche: Number(this.f['precioPorNoche'].value),
      anfitrionId: Number(user.id),
      latitud: this.form.value.latitud ?? undefined,
      longitud: this.form.value.longitud ?? undefined
    };

    this.alojamientoService.crearAlojamiento(payload).subscribe({
      next: async (resp) => {
        // Subir imágenes si el usuario agregó URLs
        if (resp?.id) {
          await this.subirImagenesSiCorresponde(Number(resp.id));
        }
        this.successMessage = 'Alojamiento publicado correctamente';
        this.router.navigate(['/alojamientos/gestion']);
      },
      error: (err) => {
        // Manejo de errores según backend
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.errorMessage = err.error;
        } else if (err?.error?.message) {
          this.errorMessage = err.error.message;
        } else if (err.status === 400) {
          this.errorMessage = 'Datos inválidos. Verifica la información ingresada.';
        } else if (err.status === 404) {
          this.errorMessage = 'No se encontró el recurso solicitado (verifica tu cuenta de anfitrión).';
        } else {
          this.errorMessage = 'Error al publicar el alojamiento. Inténtalo nuevamente.';
        }
      },
      complete: () => {
        this.isSubmitting = false;
      }
    });
  }
  private async subirImagenesSiCorresponde(alojamientoId: number) {
    const nuevas = (this.imagenesList || []).filter(i => i.isNew && i.url && i.url.trim().length > 0);
    for (const item of nuevas) {
      const url = item.url.trim();
      try {
        const created = await lastValueFrom(this.imagenService.agregarImagen({ alojamientoId, url }));
        // actualizar item con id asignado
        item.id = created?.id;
        item.isNew = false;
      } catch (_) {
        // Ignorar fallos individuales por ahora; se podría notificar por imagen fallida
      }
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadProgress = 0;
    }
  }

  onMapSelection(lngLat: [number, number] | null) {
    this.selectedLngLat = lngLat;
    if (lngLat) {
      const [lng, lat] = lngLat;
      this.form.patchValue({ latitud: lat, longitud: lng });
    } else {
      this.form.patchValue({ latitud: null, longitud: null });
    }
  }

  onManualCoordsChange() {
    const lat = this.form.value.latitud;
    const lng = this.form.value.longitud;
    if (typeof lat === 'number' && typeof lng === 'number') {
      this.selectedLngLat = [lng, lat];
    } else {
      this.selectedLngLat = null;
    }
  }

  async uploadSelectedImage() {
    if (!this.selectedFile) return;
    this.errorMessage = '';
    this.uploadProgress = 0;
    try {
      const { result } = await lastValueFrom(this.imageUpload.uploadWithProgress(this.selectedFile, 'alojamientos'));
      if (result?.url) {
        this.imagenesList = [...this.imagenesList, { url: result.url, isNew: true }];
      }
      this.selectedFile = null;
      this.uploadProgress = 100;
    } catch (err: any) {
      this.errorMessage = err?.error?.message || 'No se pudo subir la imagen.';
      this.uploadProgress = 0;
    }
  }

  agregarImagenUrl() {
    const url = (this.nuevaImagenUrl || '').trim();
    if (!url) return;
    this.imagenesList = [...this.imagenesList, { url, isNew: true }];
    this.nuevaImagenUrl = '';
  }

  async quitarImagen(item: { id?: number; url: string; isNew?: boolean }) {
    // Si vino de Cloudinary y tienes publicId (no lo guardamos aquí para simpleza),
    // podrías llamar imageUpload.deleteByPublicId(publicId) antes de quitarlo.
    // Si existe en backend (tiene id), eliminar allí
    if (this.isEdit && this.editId != null && item.id) {
      try {
        await lastValueFrom(this.imagenService.eliminarImagen(item.id));
      } catch (_) {
        // En caso de error, igualmente lo quitamos de la UI; podríamos mostrar error si prefieres
      }
    }
    // Quitar de la lista local
    this.imagenesList = this.imagenesList.filter(i => i !== item);
  }
}
