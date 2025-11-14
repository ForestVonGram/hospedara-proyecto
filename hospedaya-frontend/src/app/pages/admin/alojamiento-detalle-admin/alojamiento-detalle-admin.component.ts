import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { Alojamiento, AlojamientoService, AlojamientoUpdateRequest } from '../../../services/alojamiento.service';
import { Reserva, ReservaService } from '../../../services/reserva.service';
import { ComentarioResponse, ComentarioService } from '../../../services/comentario.service';
import { MapService } from '../../../mapbox/map-service';
import { ServicioDTO, ServicioService } from '../../../services/servicio.service';
import { AlojamientoServicioService } from '../../../services/alojamiento-servicio.service';

@Component({
  selector: 'app-alojamiento-detalle-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FormsModule],
  templateUrl: './alojamiento-detalle-admin.component.html',
  styleUrls: ['./alojamiento-detalle-admin.component.css']
})
export class AlojamientoDetalleAdminComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private alojamientoService = inject(AlojamientoService);
  private reservaService = inject(ReservaService);
  private comentarioService = inject(ComentarioService);
  private mapService = inject(MapService);
  private servicioService = inject(ServicioService);
  private alojamientoServicioService = inject(AlojamientoServicioService);

  alojamiento?: Alojamiento;
  reservas: Reserva[] = [];
  comentarios: ComentarioResponse[] = [];

  // Servicios
  serviciosCatalogo: ServicioDTO[] = [];
  selectedServicios = new Set<number>();
  relacionesActuales = new Map<number, number>();

  cargando = false;
  guardando = false;
  error: string | null = null;
  mensaje: string | null = null;

  // Mapa / ubicación
  private mapInitialized = false;
  selectedImageUrl?: string;
  selectedLngLat: [number, number] | null = null;
  selectedAddress: string = '';

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (!id || isNaN(id)) {
      this.error = 'Alojamiento inválido';
      return;
    }

    // Catálogo de servicios para checkboxes
    this.cargarServiciosCatalogo();

    this.cargarAlojamiento(id);
  }

  private cargarAlojamiento(id: number): void {
    this.cargando = true;
    this.error = null;

    this.alojamientoService.obtener(id).subscribe({
      next: (a) => {
        const precio = Number((a as any).precioPorNoche);
        this.alojamiento = { ...a, precioPorNoche: isNaN(precio) ? 0 : precio } as any;
        this.cargando = false;

        // Inicializar mapa siempre para permitir seleccionar ubicación
        this.initializeMap();

        // Cargar actividad y servicios asociados
        this.cargarActividadAlojamiento(id);
        this.cargarServiciosAlojamiento(id);
      },
      error: () => {
        this.error = 'No se pudo cargar la información del alojamiento.';
        this.cargando = false;
      }
    });
  }

  private cargarActividadAlojamiento(alojamientoId: number): void {
    // Reservas asociadas al alojamiento
    this.reservaService.listarTodas().subscribe({
      next: (rs) => {
        this.reservas = (rs || []).filter(r => r.alojamientoId === alojamientoId);
      },
      error: () => {
        this.reservas = [];
      }
    });

    // Comentarios del alojamiento
    this.comentarioService.porAlojamiento(alojamientoId).subscribe({
      next: (cs) => {
        this.comentarios = cs || [];
      },
      error: () => {
        this.comentarios = [];
      }
    });
  }

  private cargarServiciosCatalogo(): void {
    this.servicioService.listar().subscribe({
      next: (list) => {
        this.serviciosCatalogo = list || [];
      },
      error: () => {
        this.serviciosCatalogo = [];
      }
    });
  }

  private cargarServiciosAlojamiento(alojamientoId: number): void {
    this.alojamientoServicioService.listarPorAlojamiento(alojamientoId).subscribe({
      next: (rels) => {
        this.relacionesActuales.clear();
        this.selectedServicios.clear();
        (rels || []).forEach(r => {
          const sid = Number(r.servicioId);
          const relId = Number(r.relacionId);
          if (!Number.isNaN(sid) && !Number.isNaN(relId)) {
            this.relacionesActuales.set(sid, relId);
            this.selectedServicios.add(sid);
          }
        });
      },
      error: () => {
        this.relacionesActuales.clear();
        this.selectedServicios.clear();
      }
    });
  }

  private initializeMap(): void {
    if (this.mapInitialized || !this.alojamiento) return;

    setTimeout(() => {
      this.mapService.create('map-alojamiento-admin');

      // Si ya hay coordenadas, centrar y dibujar marcador inicial
      if (this.alojamiento?.latitud != null && this.alojamiento?.longitud != null) {
        this.mapService.setMarker(this.alojamiento.longitud, this.alojamiento.latitud);
        this.selectedLngLat = [this.alojamiento.longitud, this.alojamiento.latitud];
        this.selectedAddress = this.alojamiento.direccion;
      }

      // Permitir al admin cambiar la ubicación haciendo clic en el mapa
      this.mapService.addMarker().subscribe(async (marker) => {
        const lng = marker.lng;
        const lat = marker.lat;
        this.selectedLngLat = [lng, lat];

        if (this.alojamiento) {
          this.alojamiento.latitud = lat;
          this.alojamiento.longitud = lng;
        }

        // Actualizar dirección usando geocodificación inversa (similar a edición de anfitrión)
        await this.getAddressFromCoordinates(lng, lat);
        if (this.alojamiento && this.selectedAddress) {
          this.alojamiento.direccion = this.selectedAddress;
        }
      });

      this.mapInitialized = true;
    }, 100);
  }

  resolverImg(url?: string): string {
    if (!url) {
      return 'https://images.unsplash.com/photo-1505691723518-36a5ac3be353?q=80&w=1200&auto=format&fit=crop';
    }
    return url.startsWith('http') ? url : `http://localhost:8080${url}`;
  }

  private async getAddressFromCoordinates(lng: number, lat: number): Promise<void> {
    const MAPBOX_TOKEN = 'pk.eyJ1IjoiaG9zcGVkYXlhZG1pbiIsImEiOiJjbWh3Zmp5dWgwNmIwMnJwcWUzczNzY20yIn0.7eSUoU-uS-rYu5S18X_OQA';
    try {
      const response = await fetch(
        `https://api.mapbox.com/geocoding/v5/mapbox.places/${lng},${lat}.json?access_token=${MAPBOX_TOKEN}&language=es`
      );
      const data = await response.json();
      if (data.features && data.features.length > 0) {
        this.selectedAddress = data.features[0].place_name;
      } else {
        this.selectedAddress = `${lat.toFixed(6)}, ${lng.toFixed(6)}`;
      }
    } catch (error) {
      console.error('Error al obtener la dirección:', error);
      this.selectedAddress = `${lat.toFixed(6)}, ${lng.toFixed(6)}`;
    }
  }

  openImage(url?: string): void {
    this.selectedImageUrl = this.resolverImg(url);
  }

  closeImage(): void {
    this.selectedImageUrl = undefined;
  }

  toggleServicio(id: number, checked: boolean): void {
    if (checked) this.selectedServicios.add(id);
    else this.selectedServicios.delete(id);
  }

  private async syncServicios(alojamientoId: number): Promise<void> {
    const actuales = new Set<number>(this.relacionesActuales.keys());
    const deseados = new Set<number>(this.selectedServicios);

    // Añadir nuevos servicios asignados
    for (const sid of deseados) {
      if (!actuales.has(sid)) {
        try {
          const created = await lastValueFrom(
            this.alojamientoServicioService.asignar({ alojamientoId, servicioId: sid })
          );
          this.relacionesActuales.set(sid, Number(created.relacionId));
        } catch {
          // ignorar errores individuales
        }
      }
    }

    // Eliminar servicios desmarcados
    for (const sid of actuales) {
      if (!deseados.has(sid)) {
        const relId = this.relacionesActuales.get(sid);
        if (relId != null) {
          try {
            await lastValueFrom(this.alojamientoServicioService.eliminarRelacion(Number(relId)));
          } catch {
            // ignorar errores individuales
          }
          this.relacionesActuales.delete(sid);
        }
      }
    }
  }

  async guardarCambios(): Promise<void> {
    if (!this.alojamiento) return;
    this.guardando = true;
    this.error = null;
    this.mensaje = null;

    const update: AlojamientoUpdateRequest = {
      nombre: this.alojamiento.titulo,
      descripcion: this.alojamiento.descripcion,
      direccion: this.alojamiento.direccion,
      precioPorNoche: typeof this.alojamiento.precioPorNoche === 'string'
        ? Number(this.alojamiento.precioPorNoche)
        : this.alojamiento.precioPorNoche as number,
      maxHuespedes: this.alojamiento.maxHuespedes,
      latitud: this.alojamiento.latitud,
      longitud: this.alojamiento.longitud
    };

    try {
      const resp = await lastValueFrom(
        this.alojamientoService.actualizarAlojamiento(this.alojamiento.id, update)
      );
      // Sincronizar servicios asignados/desasignados
      await this.syncServicios(resp.id);

      this.mensaje = 'Cambios guardados correctamente.';
      this.alojamiento = {
        id: resp.id,
        titulo: resp.titulo,
        descripcion: resp.descripcion,
        direccion: resp.direccion,
        precioPorNoche: resp.precioPorNoche,
        maxHuespedes: resp.maxHuespedes,
        anfitrionId: resp.anfitrionId,
        latitud: resp.latitud,
        longitud: resp.longitud,
        imagenes: resp.imagenes || [],
        servicios: resp.servicios || []
      };
    } catch {
      this.error = 'No se pudieron guardar los cambios del alojamiento.';
    } finally {
      this.guardando = false;
    }
  }

  volverListado(): void {
    this.router.navigate(['/admin/alojamientos']);
  }
}
