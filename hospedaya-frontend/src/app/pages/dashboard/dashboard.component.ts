import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import {HeaderComponent} from '../../shared/components/header/header.component';
import {MapService} from '../../mapbox/map-service';
import {PlacesService} from '../../mapbox/places/places-service';
import {PlaceDTO} from '../../mapbox/places/places-dto';
import {MarkerDTO} from '../../mapbox/marker-dto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, AfterViewInit {
  user?: UsuarioProfile;

  destino = '';
  checkin = '';
  checkout = '';
  huespedes = 1;

  alojamientos: Alojamiento[] = [];
  loadingAlojamientos = false;
  errorAlojamientos: string | null = null;

  // Marcadores para el mapa (solo alojamientos con coordenadas)
  get mapMarkers() {
    return (this.alojamientos || [])
      .map(a => ({
        lng: a.longitud != null ? Number(a.longitud) : NaN,
        lat: a.latitud != null ? Number(a.latitud) : NaN,
        popup: a.titulo
      }))
      .filter(m => Number.isFinite(m.lat) && Number.isFinite(m.lng));
  }

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private auth: AuthService,
    private alojService: AlojamientoService,
    private mapService: MapService,
    private placesService: PlacesService
  ) {}

  ngOnInit(): void {
    // Cargar rápido desde cache y luego confirmar con backend
    this.user = this.auth.getUser();
    this.usuarioService.me().subscribe({ next: (u) => (this.user = u), error: () => (this.user = undefined) });

    // Inicializa el mapa con la configuración predeterminada
    this.mapService.create();

    // Cargar alojamientos reales
    this.loadingAlojamientos = true;
    this.alojService.listar().subscribe({
      next: (list) => {
        this.alojamientos = list || [];
        this.loadingAlojamientos = false;
        // Dibujar marcadores de alojamientos reales en el mapa
        this.drawAlojamientosOnMap();
      },
      error: () => {
        this.errorAlojamientos = 'No se pudieron cargar los alojamientos.';
        this.loadingAlojamientos = false;
      }
    });
  }

  showMenu = false;

  avatarUrl(): string | null {
    const u = this.user?.fotoPerfilUrl;
    return u ? (u.startsWith('http') ? u : `http://localhost:8080${u}`) : null;
  }

  imagenPrincipal(a: Alojamiento): string {
    const img = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    if (!img) {
      return 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=1200&auto=format&fit=crop';
    }
    return img.startsWith('http') ? img : `http://localhost:8080${img}`;
  }

  toggleMenu() { this.showMenu = !this.showMenu; }
  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  reservar(a: Alojamiento) {
    this.router.navigate(['/alojamientos', a.id, 'reservar']);
  }

  buscar() {
    // Navega a la página de resultados con los parámetros de búsqueda
    this.router.navigate(['/resultados'], {
      queryParams: {
        destino: this.destino,
        checkin: this.checkin,
        checkout: this.checkout,
        huespedes: this.huespedes
      }
    });
  }

  private drawAlojamientosOnMap(): void {
    const markers: MarkerDTO[] = this.alojamientos
      .filter(a => a.latitud != null && a.longitud != null)
      .map(a => ({
        id: a.id,
        title: a.titulo,
        photoUrl: this.imagenPrincipal(a),
        location: {
          latitud: a.latitud!,
          longitud: a.longitud!
        }
      }));

    if (markers.length > 0) {
      this.mapService.drawMarkers(markers);
    }
  }

  public mapItemToMarker(places: PlaceDTO[]): MarkerDTO[] {
    return places.map((item) => ({
      id: item.id,
      location: item.address.location,
      title: item.title,
      photoUrl: item.images[0] || "",
    }));
  }

  ngAfterViewInit(): void {
    // El mapa se inicializa en ngOnInit
  }
}
