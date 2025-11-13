import { Component, ElementRef, EventEmitter, Inject, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild, ContentChildren, QueryList, AfterContentInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import mapboxgl, { Map, Marker } from 'mapbox-gl';
import { MAPBOX_TOKEN } from './mapbox.config';
import { MarkerComponent } from './marker.component';

export interface MapMarker {
  lng: number;
  lat: number;
  popup?: string;
  color?: string;
  scale?: number;
  html?: string; // si se proporciona, usa elemento HTML personalizado
}

@Component({
  selector: 'app-mapbox-map',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="map-wrapper">
      <div #mapContainer class="map-container"></div>
      <div *ngIf="!hasToken" class="map-warning">
        Falta configurar el token de Mapbox. Agrega tu token público en el meta tag
        <code>&lt;meta name=\"mapbox-token\" content=\"pk.xxxxxx\"&gt;</code> dentro de <code>src/index.html</code>.
      </div>
    </div>
  `,
  styles: [`
    .map-wrapper { position: relative; width: 100%; }
    .map-container { width: 100%; height: 380px; border-radius: 12px; overflow: hidden; }
    .map-warning { padding: 12px; background: #fff3cd; color: #664d03; border: 1px solid #ffecb5; border-radius: 8px; font-size: 14px; }
  `]
})
export class MapboxMapComponent implements OnInit, OnDestroy, OnChanges, AfterContentInit {
  @ViewChild('mapContainer', { static: true }) mapContainer!: ElementRef<HTMLDivElement>;

  @Input() style: string = 'mapbox://styles/mapbox/streets-v12';
  @Input() center: [number, number] = [-74.0817, 4.6097]; // Bogotá por defecto
  @Input() zoom = 10;

  // Marcadores de solo visualización
  @Input() markers: MapMarker[] = [];

  // Modo selección de una ubicación
  @Input() selectable = false;
  @Input() selectedLngLat: [number, number] | null = null;
  @Output() selectedLngLatChange = new EventEmitter<[number, number] | null>();

  @ContentChildren(MarkerComponent) contentMarkers?: QueryList<MarkerComponent>;

  private map?: Map;
  private displayMarkers: Marker[] = [];
  private pickerMarker?: Marker;
  hasToken = true;

  constructor(@Inject(MAPBOX_TOKEN) private token: string) {}

  ngOnInit(): void {
    this.hasToken = !!this.token;
    if (!this.hasToken) return;

    mapboxgl.accessToken = this.token;

    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: this.style,
      center: this.center,
      zoom: this.zoom,
      attributionControl: true,
    });

    this.map.on('load', () => {
      this.renderMarkers();
      this.setupPicker();
      this.fitToMarkersIfAny();
    });

    // Resize on next tick in case parent is inside lazy view
    setTimeout(() => this.map && this.map.resize(), 0);
  }

  ngAfterContentInit(): void {
    // Re-render markers whenever projected <app-marker> children change
    this.contentMarkers?.changes.subscribe(() => {
      this.renderMarkers();
      this.fitToMarkersIfAny();
    });
    // Initial render in case content is already present
    this.renderMarkers();
    this.fitToMarkersIfAny();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.map) {
      if (changes['markers']) {
        this.renderMarkers();
        this.fitToMarkersIfAny();
      }
      if (changes['selectable'] || changes['selectedLngLat']) {
        this.setupPicker();
      }
      if (changes['center'] && this.center) {
        this.map.setCenter(this.center);
      }
      if (changes['zoom'] && this.zoom != null) {
        this.map.setZoom(this.zoom);
      }
    }
  }

  private allMarkers(): MapMarker[] {
    const inputMarkers = Array.isArray(this.markers) ? this.markers : [];
const contentMarkers = (this.contentMarkers?.toArray() || []).map(cm => ({
      lng: Number(cm.lng),
      lat: Number(cm.lat),
      popup: cm.popup,
      color: cm.color,
      scale: typeof (cm as any).scale === 'number' ? Number((cm as any).scale) : undefined,
      html: (cm as any).html
    }));
    return [...inputMarkers, ...contentMarkers];
  }

  private renderMarkers() {
    if (!this.map) return;
    // limpiar marcadores actuales
    this.displayMarkers.forEach(m => m.remove());
    this.displayMarkers = [];
    // agregar nuevos (input + content children)
this.allMarkers().forEach(m => {
      if (!Number.isFinite(m.lng) || !Number.isFinite(m.lat)) return;
      // Construir opciones del marcador (color/scale o HTML personalizado)
      let options: any = {};
      if (m && typeof m.html === 'string' && m.html.trim().length > 0) {
        const el = document.createElement('div');
        el.innerHTML = m.html;
        options.element = el.firstElementChild ?? el;
      } else {
        if (m.color) options.color = m.color;
        if (typeof m.scale === 'number' && Number.isFinite(m.scale)) options.scale = m.scale;
      }
      const marker = new Marker(options).setLngLat([m.lng, m.lat]).addTo(this.map!);
      if (m.popup) {
        const popup = new mapboxgl.Popup({ offset: 24 }).setText(m.popup);
        marker.setPopup(popup);
      }
      this.displayMarkers.push(marker);
    });
  }

  private setupPicker() {
    if (!this.map) return;

    // limpiar picker previo
    this.pickerMarker?.remove();
    this.pickerMarker = undefined;

    if (!this.selectable) {
      // quitar handler de click si existe
      this.map.off('click', this.onMapClick as any);
      return;
    }

    // crear marker si ya hay coordenadas
    if (this.selectedLngLat) {
      this.pickerMarker = new Marker({ draggable: true })
        .setLngLat(this.selectedLngLat)
        .addTo(this.map);
      this.pickerMarker.on('dragend', () => {
        const lngLat = this.pickerMarker!.getLngLat();
        const value: [number, number] = [lngLat.lng, lngLat.lat];
        this.selectedLngLat = value;
        this.selectedLngLatChange.emit(value);
      });
      // centrar suavemente
      this.map.easeTo({ center: this.selectedLngLat, zoom: Math.max(this.zoom, 13) });
    }

    // manejar click para seleccionar
    this.map.on('click', this.onMapClick as any);
  }

  private onMapClick = (e: mapboxgl.MapMouseEvent) => {
    if (!this.map || !this.selectable) return;
    const lngLat: [number, number] = [e.lngLat.lng, e.lngLat.lat];

    if (!this.pickerMarker) {
      this.pickerMarker = new Marker({ draggable: true }).setLngLat(lngLat).addTo(this.map);
      this.pickerMarker.on('dragend', () => {
        const p = this.pickerMarker!.getLngLat();
        const value: [number, number] = [p.lng, p.lat];
        this.selectedLngLat = value;
        this.selectedLngLatChange.emit(value);
      });
    } else {
      this.pickerMarker.setLngLat(lngLat);
    }

    this.selectedLngLat = lngLat;
    this.selectedLngLatChange.emit(lngLat);
  };

  private fitToMarkersIfAny() {
    if (!this.map) return;

    const points: [number, number][] = [];
    this.allMarkers().forEach(m => {
      const lng = Number((m as any).lng);
      const lat = Number((m as any).lat);
      if (Number.isFinite(lng) && Number.isFinite(lat)) {
        points.push([lng, lat]);
      }
    });
    if (this.selectable && this.selectedLngLat && Array.isArray(this.selectedLngLat)) {
      const [lng, lat] = this.selectedLngLat;
      if (Number.isFinite(lng) && Number.isFinite(lat)) points.push([lng, lat]);
    }

    if (points.length === 0) return;

    if (points.length === 1) {
      this.map.easeTo({ center: points[0], zoom: Math.max(this.map.getZoom(), 13) });
      return;
    }

    const bounds = new mapboxgl.LngLatBounds(points[0], points[0]);
    for (let i = 1; i < points.length; i++) bounds.extend(points[i] as any);
    this.map.fitBounds(bounds, { padding: 40, duration: 700, maxZoom: 15 });
  }

  ngOnDestroy(): void {
    this.map?.off('click', this.onMapClick as any);
    this.displayMarkers.forEach(m => m.remove());
    this.pickerMarker?.remove();
    this.map?.remove();
  }
}
