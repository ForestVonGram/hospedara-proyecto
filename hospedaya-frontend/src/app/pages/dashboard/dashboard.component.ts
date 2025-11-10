import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import {HeaderComponent} from '../../shared/components/header/header.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
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
  loadingAlojamientos = false;
  errorAlojamientos: string | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private auth: AuthService,
    private alojService: AlojamientoService
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
    this.router.navigate(['/buscar'], {
      queryParams: {
        destino: this.destino,
        checkin: this.checkin,
        checkout: this.checkout,
        huespedes: this.huespedes
      }
    });
  }
}
