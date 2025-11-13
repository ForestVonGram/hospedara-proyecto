import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import mapboxgl, { LngLatLike, Map, Marker, MapMouseEvent } from 'mapbox-gl';
import { MarkerDTO } from '../mapbox/marker-dto';

@Injectable({
  providedIn: 'root',
})
export class MapService implements OnDestroy{
  private map?: Map;
  private markers: Marker[] = [];
  private currentLocation: LngLatLike = [-75.6727, 4.53252];
  private readonly MAPBOX_TOKEN = 'pk.eyJ1IjoiaG9zcGVkYXlhZG1pbiIsImEiOiJjbWh3Zmp5dWgwNmIwMnJwcWUzczNzY20yIn0.7eSUoU-uS-rYu5S18X_OQA';
  private destroy$ = new Subject<void>();
  private markerClick$ = new Subject<{ lat: number; lng: number }>();

  constructor() {
    mapboxgl.accessToken = this.MAPBOX_TOKEN;
  }

  public create(containerId: string = 'map'): void {
    if (this.map) {
      this.map.remove();
    }

    this.map = new mapboxgl.Map({
      container: containerId,
      style: 'mapbox://styles/mapbox/standard',
      center: this.currentLocation,
      zoom: 17,
      pitch: 45,
    });

    this.map.addControl(new mapboxgl.NavigationControl());
    this.map.addControl(
      new mapboxgl.GeolocateControl({
        positionOptions: { enableHighAccuracy: true },
        trackUserLocation: true,
      })
    );
  }

  public drawMarkers(places: MarkerDTO[]): void {
    if (!this.map) return;

    places.forEach(({ id, title, photoUrl, location }) => {
      const popupHtml = `
        <div style="text-align: center; padding: 8px;">
          <strong style="font-size: 14px; display: block; margin-bottom: 8px;">${title}</strong>
          <div style="margin-bottom: 8px;">
            <img src="${photoUrl}" alt="Imagen" style="width: 100%; height: 100px; object-fit: cover; border-radius: 8px;">
          </div>
          <a href="/alojamientos/${id}" style="display: inline-block; background: linear-gradient(90deg, #7c3aed, #d946ef); color: white; padding: 8px 16px; text-decoration: none; border-radius: 20px; font-weight: 600; font-size: 13px;">Ver detalles</a>
        </div>
      `;

      new mapboxgl.Marker({ color: 'red' })
        .setLngLat([location.longitud, location.latitud])
        .setPopup(new mapboxgl.Popup().setHTML(popupHtml))
        .addTo(this.map!);
    });
  }

  public addMarker(): Observable<mapboxgl.LngLat> {
    return new Observable((observer) => {
      if (!this.map) {
        observer.error('Mapa no inicializado');
        return;
      }

      const onClick = (e: MapMouseEvent) => {
        this.clearMarkers();
        const marker = new mapboxgl.Marker({color: 'red'})
          .setLngLat(e.lngLat)
          .addTo(this.map!);

        this.markers.push(marker);
        // Emite las coordenadas del marcador al observador
        observer.next(marker.getLngLat());
      };

      this.map.on('click', onClick);

      return () => {
        this.map?.off('click', onClick);
      };
    });
  }
  public clearMarkers(): void {
    this.markers.forEach(marker => marker.remove());
    this.markers = [];
  }

  public setMarker(lng: number, lat: number): void {
    if (!this.map) return;
    
    this.clearMarkers();
    const marker = new mapboxgl.Marker({ color: 'red' })
      .setLngLat([lng, lat])
      .addTo(this.map);
    
    this.markers.push(marker);
    this.map.flyTo({ center: [lng, lat], zoom: 15 });
  }

  public get mapInstance(): Map | undefined {
    return this.map;
  }

  /** Limpieza al destruir el servicio */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    if (this.map) {
      this.map.remove();
      this.map = undefined;
    }
  }
}
