import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService, Usuario } from '../../services/auth.service';
import { AlojamientoService, AlojamientoResponseDTO } from '../../services/alojamiento.service';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { Reserva, ReservaService } from '../../services/reserva.service';

@Component({
  selector: 'app-dashboard-anfitrion',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './dashboard-anfitrion.component.html',
  styleUrl: './dashboard-anfitrion.component.css'
})
export class DashboardAnfitrionComponent implements OnInit {
  constructor(
    private auth: AuthService,
    private alojamientoService: AlojamientoService,
    private reservaService: ReservaService,
    private router: Router
  ) {}

  user: Usuario | null = null;
  cargando = false;
  error = '';
  alojamientos: AlojamientoResponseDTO[] = [];

  // Sección reservas
  reservas: Reserva[] = [];
  cargandoReservas = false;
  errorReservas = '';

  // KPIs básicos
  get totalAlojamientos(): number {
    return this.alojamientos.length;
  }

  get reservasActivas(): number {
    // Si hubiera estado, podríamos filtrar por estado === 'CONFIRMADA' o similar
    return this.reservas.length;
  }

  get gananciasEstimadas(): number {
    // Simple estimación: suma precioPorNoche (puede ajustarse cuando haya datos reales)
    return this.alojamientos.reduce((acc, a) => acc + (a.precioPorNoche || 0), 0);
  }

  ngOnInit(): void {
    const u = this.auth.getUser() as Usuario | null;
    if (!u) {
      this.router.navigate(['/login']);
      return;
    }
    if (!u.rol || u.rol !== 'ANFITRION') {
      this.router.navigate(['/register-host']);
      return;
    }

    this.user = u;
    this.cargarAlojamientos(Number(u.id));
  }

  private cargarReservasParaAlojamientos() {
    const ids = new Set(this.alojamientos.map(a => a.id));
    if (ids.size === 0) {
      this.reservas = [];
      return;
    }
    this.cargandoReservas = true;
    this.errorReservas = '';
    this.reservaService.listarTodas().subscribe({
      next: (list) => {
        this.reservas = (list || []).filter(r => ids.has(Number(r.alojamientoId)));
      },
      error: (err) => {
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.errorReservas = err.error;
        } else if (err?.error?.message) {
          this.errorReservas = err.error.message;
        } else {
          this.errorReservas = 'No se pudieron cargar las reservas de tus alojamientos.';
        }
      },
      complete: () => (this.cargandoReservas = false)
    });
  }

  cargarAlojamientos(anfitrionId: number) {
    this.cargando = true;
    this.error = '';
    this.alojamientoService.listarPorAnfitrion(anfitrionId).subscribe({
      next: (data) => {
        this.alojamientos = data || [];
        this.cargarReservasParaAlojamientos();
      },
      error: (err) => {
        // Si el backend devuelve 404 cuando no hay alojamientos, lo tratamos como lista vacía
        if (err?.status === 404) {
          this.alojamientos = [];
          this.error = '';
          // Sin alojamientos, no habrá reservas
          this.reservas = [];
          return;
        }
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.error = err.error;
        } else if (err?.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = 'No se pudieron cargar tus alojamientos. Inténtalo más tarde.';
        }
      },
      complete: () => (this.cargando = false)
    });
  }

  irANuevo() {
    this.router.navigate(['/alojamientos/nuevo']);
  }

  irAGestion() {
    this.router.navigate(['/alojamientos/gestion']);
  }

  verDetalle(alojamiento: AlojamientoResponseDTO) {
    // No existe ruta de detalle de alojamiento en app.routes; redirigimos a gestión como fallback
    this.irAGestion();
  }

  alojamientoDeReserva(r: Reserva): AlojamientoResponseDTO | undefined {
    return this.alojamientos.find(a => Number(a.id) === Number(r.alojamientoId));
  }

  resolverImg(a?: AlojamientoResponseDTO): string {
    if (!a) return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
    const url = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
  }
}
