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
import { ServicioDTO, ServicioService } from '../../services/servicio.service';
import { AlojamientoServicioService } from '../../services/alojamiento-servicio.service';

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

  // Servicios
  servicios: ServicioDTO[] = [];
  selectedServicios = new Set<number>();
  // Mapa de relaciones actuales (servicioId -> relacionId) cuando editando
  relacionesActuales = new Map<number, number>();
  nuevoServicioNombre = '';
  nuevoServicioDescripcion = '';

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private alojamientoService: AlojamientoService,
    private imagenService: ImagenAlojamientoService,
    private imageUpload: ImageUploadService,
    private router: Router,
    private route: ActivatedRoute,
    private servicioService: ServicioService,
    private alojamientoServicioService: AlojamientoServicioService
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

    // Cargar catálogo de servicios
    this.cargarServicios();

    this.form = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(120)]],
      descripcion: ['', [Validators.required, Validators.maxLength(1000)]],
      direccion: ['', [Validators.required, Validators.maxLength(200)]],
      precioPorNoche: [null, [Validators.required, Validators.min(0)]],
      maxHuespedes: [null, [Validators.min(1)]],
      habitaciones: [null, [Validators.min(0)]],
      banos: [null, [Validators.min(0)]],
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
              const parsed = this.parseMetaFromDescripcion(dto.descripcion || '');
              this.form.patchValue({
                nombre: dto.titulo,
                descripcion: parsed.base,
                direccion: dto.direccion,
                precioPorNoche: dto.precioPorNoche,
                maxHuespedes: (dto as any).maxHuespedes ?? null,
                habitaciones: parsed.habitaciones,
                banos: parsed.banos,
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

          // Preseleccionar servicios ya asociados en modo edición
          this.alojamientoServicioService.listarPorAlojamiento(id).subscribe({
            next: (rels) => {
              this.relacionesActuales.clear();
              (rels || []).forEach(r => {
                this.selectedServicios.add(Number(r.servicioId));
                this.relacionesActuales.set(Number(r.servicioId), Number(r.relacionId));
              });
            },
            error: () => {}
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
      // Construir descripción combinando base + meta (habitaciones/baños)
      const habE = this.form.value.habitaciones;
      const banE = this.form.value.banos;
      const descBaseE: string = this.f['descripcion'].value || '';
      const descExtraE = (habE != null || banE != null) ? `\nHabitaciones: ${habE ?? 0}\nBaños: ${banE ?? 0}` : '';

      const update: AlojamientoUpdateRequest = {
        nombre: this.f['nombre'].value,
        descripcion: (descBaseE + descExtraE).trim(),
        direccion: this.f['direccion'].value,
        // Normalizar precio admitiendo formatos "es-CO" (500.000,50)
        precioPorNoche: this.parsePrecio(this.f['precioPorNoche'].value),
        maxHuespedes: this.form.value.maxHuespedes ?? undefined,
        latitud: this.form.value.latitud ?? undefined,
        longitud: this.form.value.longitud ?? undefined
      };

      this.alojamientoService.actualizarAlojamiento(this.editId, update).subscribe({
        next: async (resp: AlojamientoResponseDTO) => {
          // Subir nuevas imágenes añadidas (si son URLs nuevas)
          await this.subirImagenesSiCorresponde(this.editId!);
          // Sincronizar servicios (añadir nuevos / eliminar desmarcados)
          await this.syncServicios(this.editId!);
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

    // Enviar habitaciones/baños como parte de la descripción para persistirlos (backend aún no los modela)
    const hab = this.form.value.habitaciones;
    const ban = this.form.value.banos;
    const descBase: string = this.f['descripcion'].value || '';
    const descExtra = (hab != null || ban != null) ? `\nHabitaciones: ${hab ?? 0}\nBaños: ${ban ?? 0}` : '';

    const payload: AlojamientoCreateRequest = {
      nombre: this.f['nombre'].value,
      descripcion: (descBase + descExtra).trim(),
      direccion: this.f['direccion'].value,
      // Normalizar precio admitiendo formatos "es-CO" (500.000,50)
      precioPorNoche: this.parsePrecio(this.f['precioPorNoche'].value),
      maxHuespedes: this.form.value.maxHuespedes ?? undefined,
      anfitrionId: Number(user.id),
      latitud: this.form.value.latitud ?? undefined,
      longitud: this.form.value.longitud ?? undefined
    };

    this.alojamientoService.crearAlojamiento(payload).subscribe({
      next: async (resp) => {
        // Subir imágenes si el usuario agregó URLs
        if (resp?.id) {
          await this.subirImagenesSiCorresponde(Number(resp.id));
          await this.asignarServiciosSeleccionados(Number(resp.id));
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

  private async asignarServiciosSeleccionados(alojamientoId: number) {
    const ids = Array.from(this.selectedServicios);
    for (const servicioId of ids) {
      try {
        await lastValueFrom(this.alojamientoServicioService.asignar({ alojamientoId, servicioId }));
      } catch (_) {
        // Continuar con los demás aunque uno falle
      }
    }
  }

  private async syncServicios(alojamientoId: number) {
    // Calcular diferencias entre seleccionados actuales y relaciones existentes
    const actuales = new Set<number>(Array.from(this.relacionesActuales.keys()));
    const deseados = new Set<number>(Array.from(this.selectedServicios));

    // Añadir nuevos
    for (const sid of deseados) {
      if (!actuales.has(sid)) {
        try {
          const created = await lastValueFrom(this.alojamientoServicioService.asignar({ alojamientoId, servicioId: sid }));
          // actualizar cache
          this.relacionesActuales.set(sid, Number(created.relacionId));
        } catch (_) {}
      }
    }

    // Eliminar deseleccionados
    for (const sid of actuales) {
      if (!deseados.has(sid)) {
        const relId = this.relacionesActuales.get(sid);
        if (relId != null) {
          try {
            await lastValueFrom(this.alojamientoServicioService.eliminarRelacion(Number(relId)));
          } catch (_) {}
          this.relacionesActuales.delete(sid);
        }
      }
    }
  }

  toggleServicio(id: number, checked: boolean) {
    if (checked) this.selectedServicios.add(id);
    else this.selectedServicios.delete(id);
  }

  crearServicio() {
    const nombre = (this.nuevoServicioNombre || '').trim();
    const descripcion = (this.nuevoServicioDescripcion || '').trim();
    if (!nombre) return;

    // Si ya existe un servicio con ese nombre (ignore case), solo selecciónalo
    const existente = (this.servicios || []).find(s => (s.nombre || '').toLowerCase() === nombre.toLowerCase());
    if (existente) {
      this.selectedServicios.add(Number(existente.id));
      this.nuevoServicioNombre = '';
      this.nuevoServicioDescripcion = '';
      return;
    }

    this.servicioService.crear({ nombre, descripcion: descripcion || undefined }).subscribe({
      next: (s) => {
        // Refrescar catálogo para mantener consistencia visual
        this.servicioService.listar().subscribe({
          next: (list) => {
            this.servicios = list || [];
            // Seleccionar el recién creado (por id devuelto o por nombre como fallback)
            const sel = (this.servicios.find(x => x.id === s.id) || this.servicios.find(x => (x.nombre || '').toLowerCase() === nombre.toLowerCase()));
            if (sel) this.selectedServicios.add(Number(sel.id));
            this.nuevoServicioNombre = '';
            this.nuevoServicioDescripcion = '';
          },
          error: () => {
            // Fallback si el refresh falla, usar el devuelto
            this.servicios.push(s);
            this.selectedServicios.add(Number(s.id));
            this.nuevoServicioNombre = '';
            this.nuevoServicioDescripcion = '';
          }
        });
      },
      error: (err) => {
        // Manejar duplicado por restricción única u otros errores de validación
        const msg = typeof err?.error === 'string' && err.error.trim().length > 0
          ? err.error
          : (err?.status === 409 ? 'El servicio ya existe.' : 'No se pudo crear el servicio.');
        this.errorMessage = msg;
      }
    });
  }

  private cargarServicios() {
    this.servicioService.listar().subscribe({
      next: (list) => this.servicios = list || [],
      error: () => this.servicios = []
    });
  }

  // Utilidad: extraer meta (habitaciones/baños) desde la descripción y devolver base limpia
  private parseMetaFromDescripcion(desc: string): { base: string; habitaciones: number | null; banos: number | null } {
    if (!desc) return { base: '', habitaciones: null, banos: null };
    const lines = desc.split(/\r?\n/);
    let habitaciones: number | null = null;
    let banos: number | null = null;
    const baseLines: string[] = [];
    const habRegex = /^\s*Habitaciones\s*:\s*(\d+)/i;
    const banRegex = /^\s*Ba[ñn]os\s*:\s*(\d+)/i;
    for (const line of lines) {
      const habM = line.match(habRegex);
      const banM = line.match(banRegex);
      if (habM) {
        habitaciones = Number(habM[1]);
        continue;
      }
      if (banM) {
        banos = Number(banM[1]);
        continue;
      }
      baseLines.push(line);
    }
    const base = baseLines.join('\n').trim();
    return { base, habitaciones, banos };
  }

  private parsePrecio(input: any): number {
    if (typeof input === 'number') return Number.isFinite(input) ? input : 0;
    if (input == null) return 0;
    let s = String(input).trim();
    if (s === '') return 0;
    // Si trae coma, asumimos formato es-CO: 500.000,50 -> 500000.50
    if (s.includes(',')) {
      s = s.replace(/\./g, '').replace(',', '.');
      const n = Number(s);
      return Number.isFinite(n) ? n : 0;
    }
    // Sin coma: si coincide patrón de miles con puntos, quitar puntos
    if (/^\d{1,3}(\.\d{3})+$/.test(s)) {
      const n = Number(s.replace(/\./g, ''));
      return Number.isFinite(n) ? n : 0;
    }
    // Caso general: usar Number directo
    const n = Number(s);
    return Number.isFinite(n) ? n : 0;
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
