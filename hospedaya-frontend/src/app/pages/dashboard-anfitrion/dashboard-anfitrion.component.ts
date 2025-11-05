import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService, Usuario } from '../../services/auth.service';
import { AlojamientoService, AlojamientoResponseDTO } from '../../services/alojamiento.service';

@Component({
  selector: 'app-dashboard-anfitrion',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-anfitrion.component.html',
  styleUrl: './dashboard-anfitrion.component.css'
})
export class DashboardAnfitrionComponent implements OnInit {
  constructor(
    private auth: AuthService,
    private alojamientoService: AlojamientoService,
    private router: Router
  ) {}

  user: Usuario | null = null;
  cargando = false;
  error = '';
  alojamientos: AlojamientoResponseDTO[] = [];

  // KPIs básicos
  get totalAlojamientos(): number {
    return this.alojamientos.length;
  }

  reservasActivas = 0; // placeholder si luego se conecta a reservas del backend

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

  cargarAlojamientos(anfitrionId: number) {
    this.cargando = true;
    this.error = '';
    this.alojamientoService.listarPorAnfitrion(anfitrionId).subscribe({
      next: (data) => (this.alojamientos = data || []),
      error: (err) => {
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
}
