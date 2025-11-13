import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlojamientoService, Alojamiento } from '../services/alojamiento.service';
import { MapboxMapComponent, MapMarker } from './mapbox-map.component';
import { MarkerComponent } from './marker.component';

@Component({
  selector: 'app-detalle-alojamiento-map',
  standalone: true,
  imports: [CommonModule, MapboxMapComponent, MarkerComponent],
  template: `
    <div class="detalle-map">
      <app-mapbox-map
        [center]="center"
        [zoom]="zoom">
        <app-marker *ngFor="let m of markers" [lng]="m.lng" [lat]="m.lat" [popup]="m.popup"></app-marker>
      </app-mapbox-map>
      <div *ngIf="loading" class="loading">Cargando mapa...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
    </div>
  `,
  styles: [`
    .detalle-map { position: relative; }
    .loading, .error { margin-top: 8px; font-size: 14px; color: #475569; }
    .error { color: #b91c1c; }
  `]
})
export class DetalleAlojamientoMapComponent implements OnChanges {
  @Input() alojamientoId?: number;

  center: [number, number] = [-74.0817, 4.6097];
  zoom = 12;
  markers: MapMarker[] = [];
  loading = false;
  error: string | null = null;

  constructor(private alojService: AlojamientoService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if ('alojamientoId' in changes) {
      this.fetch();
    }
  }

  private fetch() {
    const id = this.alojamientoId;
    if (!id || isNaN(Number(id))) {
      this.error = 'ID de alojamiento inválido';
      return;
    }
    this.loading = true;
    this.error = null;
    this.alojService.obtener(Number(id)).subscribe({
      next: (a: Alojamiento) => {
        const lat = a.latitud != null ? Number(a.latitud) : NaN;
        const lng = a.longitud != null ? Number(a.longitud) : NaN;
        if (Number.isFinite(lat) && Number.isFinite(lng)) {
          this.center = [lng, lat];
          this.zoom = 14;
          this.markers = [{ lng, lat, popup: a.titulo }];
        } else {
          this.center = [-74.0817, 4.6097];
          this.zoom = 12;
          this.markers = [];
        }
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudo cargar la ubicación del alojamiento';
        this.loading = false;
      }
    });
  }
}
