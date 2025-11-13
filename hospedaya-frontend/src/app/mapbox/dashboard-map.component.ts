import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlojamientoService } from '../services/alojamiento.service';
import { MapboxMapComponent, MapMarker } from './mapbox-map.component';
import { MarkerComponent } from './marker.component';

@Component({
  selector: 'app-dashboard-map',
  standalone: true,
  imports: [CommonModule, MapboxMapComponent, MarkerComponent],
  template: `
    <div class="dashboard-map">
      <app-mapbox-map
        [center]="[-74.0817, 4.6097]"
        [zoom]="10">
        <app-marker *ngFor="let m of markers" [lng]="m.lng" [lat]="m.lat" [popup]="m.popup"></app-marker>
      </app-mapbox-map>
      <div *ngIf="loading" class="loading">Cargando mapa...</div>
      <div *ngIf="error" class="error">{{ error }}</div>
    </div>
  `,
  styles: [`
    .dashboard-map { position: relative; }
    .loading, .error {
      margin-top: 8px;
      font-size: 14px;
      color: #475569;
    }
    .error { color: #b91c1c; }
  `]
})
export class DashboardMapComponent {
  markers: MapMarker[] = [];
  loading = true;
  error: string | null = null;

  constructor(private alojService: AlojamientoService) {
    this.alojService.listar().subscribe({
      next: (list) => {
        this.markers = (list || [])
          .map(a => ({
            lng: a.longitud != null ? Number(a.longitud) : NaN,
            lat: a.latitud != null ? Number(a.latitud) : NaN,
            popup: a.titulo
          }))
          .filter(m => Number.isFinite(m.lat) && Number.isFinite(m.lng));
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar ubicaciones en el mapa';
        this.loading = false;
      }
    });
  }
}
