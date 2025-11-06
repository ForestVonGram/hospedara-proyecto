import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService, Usuario } from '../../services/auth.service';
import { AlojamientoResponseDTO, AlojamientoService } from '../../services/alojamiento.service';
import { Reserva, ReservaService } from '../../services/reserva.service';

interface ReservaHostVista extends Reserva {
  alojamiento?: AlojamientoResponseDTO;
}

@Component({
  selector: 'app-reservas-anfitrion',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './reservas-anfitrion.component.html',
  styleUrl: './reservas-anfitrion.component.css'
})
export class ReservasAnfitrionComponent implements OnInit {
  usuario: Usuario | null = null;
  cargando = false;
  error = '';
  reservas: ReservaHostVista[] = [];

  constructor(
    private auth: AuthService,
    private router: Router,
    private alojamientoService: AlojamientoService,
    private reservaService: ReservaService
  ) {}

  ngOnInit(): void {
    const u = this.auth.getUser() as Usuario | null;
    if (!u) {
      this.router.navigate(['/login']);
      return;
    }
    if (!u.rol || u.rol !== 'ANFITRION') {
      // Solo anfitriones pueden ver esta página
      this.router.navigate(['/dashboard']);
      return;
    }
    this.usuario = u;
    this.cargarReservasDeAnfitrion(Number(u.id));
  }

  private cargarReservasDeAnfitrion(anfitrionId: number) {
    this.cargando = true;
    this.error = '';

    // 1) Cargar alojamientos del anfitrión
    this.alojamientoService.listarPorAnfitrion(anfitrionId).subscribe({
      next: (alojamientos) => {
        const idsPropios = new Set((alojamientos || []).map(a => a.id));
        const alojMap = new Map<number, AlojamientoResponseDTO>();
        (alojamientos || []).forEach(a => alojMap.set(a.id, a));

        // 2) Cargar todas las reservas y filtrar por los alojamientos del anfitrión
        this.reservaService.listarTodas().subscribe({
          next: (todas) => {
            const propias = (todas || []).filter(r => idsPropios.has(Number(r.alojamientoId)));
            this.reservas = propias.map(r => ({ ...r, alojamiento: alojMap.get(Number(r.alojamientoId)) }));
          },
          error: (err) => this.error = this.extraerMensajeError(err, 'No se pudieron cargar las reservas.'),
          complete: () => this.cargando = false
        });
      },
      error: (err) => {
        this.error = this.extraerMensajeError(err, 'No se pudieron cargar tus alojamientos.');
        this.cargando = false;
      }
    });
  }

  cancelar(reserva: ReservaHostVista) {
    if (!confirm('¿Seguro que deseas rechazar/cancelar esta reserva?')) return;
    this.reservaService.cancelar(Number(reserva.id)).subscribe({
      next: () => {
        // actualizar en memoria sin volver a cargar todo
        this.reservas = this.reservas.map(r => r.id === reserva.id ? { ...r, estado: 'CANCELADA' } : r);
      },
      error: (err) => this.error = this.extraerMensajeError(err, 'No se pudo cancelar la reserva.')
    });
  }

  // Placeholder: el backend actual no expone un endpoint para confirmar; mantenemos botón deshabilitado
  confirmar(_reserva: ReservaHostVista) {
    alert('La confirmación de reservas aún no está disponible en el backend.');
  }

  estadoClase(estado?: string): string {
    switch ((estado || '').toUpperCase()) {
      case 'CONFIRMADA': return 'confirmada';
      case 'CANCELADA': return 'cancelada';
      default: return 'pendiente';
    }
  }

  private extraerMensajeError(err: any, generico: string): string {
    if (typeof err?.error === 'string' && err.error.trim().length > 0) return err.error;
    if (err?.error?.message) return err.error.message;
    return generico;
  }
}
