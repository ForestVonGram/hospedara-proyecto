import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AlojamientoService, AlojamientoResponseDTO } from '../../services/alojamiento.service';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-gestion-alojamientos',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './gestion-alojamientos.component.html',
  styleUrl: './gestion-alojamientos.component.css'
})
export class GestionAlojamientosComponent implements OnInit {
  cargando = false;
  error = '';
  alojamientos: AlojamientoResponseDTO[] = [];
  anfitrionId: number | null = null;

  constructor(
    private auth: AuthService,
    private alojamientoService: AlojamientoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }
    if (!user.rol || user.rol !== 'ANFITRION') {
      this.router.navigate(['/register-host']);
      return;
    }

    this.anfitrionId = Number(user.id);
    this.cargarAlojamientos(this.anfitrionId);
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

  crearNuevo() {
    this.router.navigate(['/alojamientos/nuevo']);
  }

  editar(a: AlojamientoResponseDTO) {
    this.router.navigate(['/alojamientos/nuevo'], { queryParams: { editId: a.id } });
  }

  eliminar(a: AlojamientoResponseDTO) {
    if (!confirm(`¿Seguro que quieres eliminar \"${a.titulo}\"? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.cargando = true;
    this.error = '';
    this.alojamientoService.eliminarAlojamiento(a.id).subscribe({
      next: () => {
        if (this.anfitrionId != null) {
          this.cargarAlojamientos(this.anfitrionId);
        }
      },
      error: (err) => {
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.error = err.error;
        } else if (err?.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = 'No se pudo eliminar el alojamiento. Inténtalo nuevamente.';
        }
        this.cargando = false;
      }
    });
  }
}
