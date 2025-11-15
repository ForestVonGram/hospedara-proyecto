import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { Reserva, ReservaService } from '../../../services/reserva.service';
import { UsuarioAdminSummary, UsuarioService } from '../../../services/usuario.service';
import { Alojamiento, AlojamientoService } from '../../../services/alojamiento.service';

interface ReservaAdminItem extends Reserva {
  usuarioNombre?: string;
  usuarioEmail?: string;
  alojamientoTitulo?: string;
}

@Component({
  selector: 'app-reservas-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FormsModule],
  templateUrl: './reservas-admin.component.html',
  styleUrls: ['./reservas-admin.component.css']
})
export class ReservasAdminComponent implements OnInit {
  reservas: ReservaAdminItem[] = [];
  cargando = false;
  error: string | null = null;
  filtro = '';

  constructor(
    private reservaService: ReservaService,
    private usuarioService: UsuarioService,
    private alojamientoService: AlojamientoService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  get reservasFiltradas(): ReservaAdminItem[] {
    const term = this.filtro.trim().toLowerCase();
    if (!term) return this.reservas;
    return this.reservas.filter(r =>
      String(r.id).includes(term) ||
      (r.usuarioNombre || '').toLowerCase().includes(term) ||
      (r.usuarioEmail || '').toLowerCase().includes(term) ||
      (r.alojamientoTitulo || '').toLowerCase().includes(term)
    );
  }

  cancelarReserva(reserva: ReservaAdminItem): void {
    const yaCancelada = (reserva.estado || '').toUpperCase() === 'CANCELADA';
    if (yaCancelada) {
      return;
    }

    const ok = window.confirm(
      `¿Seguro que quieres cancelar la reserva #${reserva.id}?\n` +
      'Se notificará a las partes involucradas y no podrá volver a activarse desde el panel.'
    );
    if (!ok) return;

    this.cargando = true;
    this.error = null;

    this.reservaService.cancelar(reserva.id).subscribe({
      next: () => {
        // Refrescar la lista para reflejar el nuevo estado
        this.cargarDatos();
      },
      error: (err) => {
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.error = err.error;
        } else if (err?.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = 'No se pudo cancelar la reserva. Inténtalo nuevamente.';
        }
        this.cargando = false;
      }
    });
  }

  private cargarDatos(): void {
    this.cargando = true;
    this.error = null;

    Promise.all([
      this.reservaService.listarTodas().toPromise(),
      this.usuarioService.listarTodos().toPromise(),
      this.alojamientoService.listar().toPromise()
    ])
      .then(([reservas, usuarios, alojamientos]) => {
        const usuarioPorId = new Map<number, UsuarioAdminSummary>();
        (usuarios || []).forEach(u => usuarioPorId.set(u.id, u));

        const alojamientoPorId = new Map<number, Alojamiento>();
        (alojamientos || []).forEach(a => alojamientoPorId.set(a.id, a));

        this.reservas = (reservas || []).map(r => {
          const usuario = usuarioPorId.get(r.usuarioId);
          const alojamiento = alojamientoPorId.get(r.alojamientoId);
          return {
            ...r,
            usuarioNombre: usuario?.nombre,
            usuarioEmail: usuario?.email,
            alojamientoTitulo: alojamiento?.titulo
          } as ReservaAdminItem;
        });
      })
      .catch(() => {
        this.error = 'No se pudieron cargar los datos de reservas.';
      })
      .finally(() => {
        this.cargando = false;
      });
  }
}
