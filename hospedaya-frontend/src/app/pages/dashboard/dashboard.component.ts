import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { RecomendacionService } from '../../services/recomendacion.service';
import {HeaderComponent} from '../../shared/components/header/header.component';
import { DashboardMapComponent } from '../../mapbox/dashboard-map.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent, DashboardMapComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  user?: UsuarioProfile;

  destino = '';
  checkin = '';
  checkout = '';
  huespedes = 1;

  alojamientos: Alojamiento[] = [];
  recomendados: Alojamiento[] = [];
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
    private recService: RecomendacionService
  ) {}

  ngOnInit(): void {
    // Cargar rápido desde cache y luego confirmar con backend
    this.user = this.auth.getUser();
    this.usuarioService.me().subscribe({ next: (u) => (this.user = u), error: () => (this.user = undefined) });

    // Cargar alojamientos reales
    this.loadingAlojamientos = true;
    this.alojService.listar().subscribe({
      next: (list) => {
        this.alojamientos = list || [];
        this.loadingAlojamientos = false;
        // Cargar recomendaciones si hay usuario
        const uid = this.user?.id ? Number(this.user.id) : null;
        if (uid) {
          this.recService.porUsuario(uid, 8).subscribe({
            next: (recs) => {
              // mapear DTO -> modelo UI si hiciera falta; aquí el dashboard usa modelo UI, así que convertimos rápido
              this.recomendados = (recs || []).map(r => ({
                id: Number(r.id),
                titulo: r.titulo,
                descripcion: r.descripcion,
                direccion: r.direccion,
                precioPorNoche: Number(r.precioPorNoche),
                anfitrionId: (r as any).anfitrionId,
                latitud: (r as any).latitud,
                longitud: (r as any).longitud,
                imagenes: (r as any).imagenes || [],
                servicios: (r as any).servicios || []
              }));
            },
            error: () => { this.recomendados = []; }
          });
        }
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

  verDetalle(a: Alojamiento) {
    this.router.navigate(['/alojamientos', a.id]);
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
}
