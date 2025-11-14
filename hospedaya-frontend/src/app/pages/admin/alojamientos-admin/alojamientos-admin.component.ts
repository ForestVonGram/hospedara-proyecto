import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { Alojamiento, AlojamientoService } from '../../../services/alojamiento.service';
import { Reserva, ReservaService } from '../../../services/reserva.service';
import { ComentarioResponse, ComentarioService } from '../../../services/comentario.service';
import { UsuarioAdminSummary, UsuarioService } from '../../../services/usuario.service';

interface AlojamientoAdminItem extends Alojamiento {
  anfitrionNombre?: string;
  reservasCount: number;
  comentariosCount: number;
}

@Component({
  selector: 'app-alojamientos-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FormsModule],
  templateUrl: './alojamientos-admin.component.html',
  styleUrls: ['./alojamientos-admin.component.css']
})
export class AlojamientosAdminComponent implements OnInit {
  alojamientos: AlojamientoAdminItem[] = [];
  cargando = false;
  error: string | null = null;
  filtro = '';

  constructor(
    private alojamientoService: AlojamientoService,
    private reservaService: ReservaService,
    private comentarioService: ComentarioService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  get alojamientosFiltrados(): AlojamientoAdminItem[] {
    const term = this.filtro.trim().toLowerCase();
    if (!term) return this.alojamientos;
    return this.alojamientos.filter(a =>
      a.titulo.toLowerCase().includes(term) ||
      a.direccion.toLowerCase().includes(term) ||
      (a.anfitrionNombre || '').toLowerCase().includes(term)
    );
  }

  private cargarDatos(): void {
    this.cargando = true;
    this.error = null;

    Promise.all([
      this.alojamientoService.listar().toPromise(),
      this.reservaService.listarTodas().toPromise(),
      this.comentarioService.listarTodos().toPromise(),
      this.usuarioService.listarTodos().toPromise()
    ])
      .then(([alojamientos, reservas, comentarios, usuarios]) => {
        const reservasPorAlojamiento = this.agruparReservasPorAlojamiento(reservas || []);
        const comentariosPorAlojamiento = this.agruparComentariosPorAlojamiento(comentarios || []);
        const anfitrionNombrePorId = this.mapaAnfitrionNombre(usuarios || []);

        this.alojamientos = (alojamientos || []).map(a => {
          const reservasCount = reservasPorAlojamiento.get(a.id) || 0;
          const comentariosCount = comentariosPorAlojamiento.get(a.id) || 0;

          return {
            ...a,
            anfitrionNombre: a.anfitrionId ? (anfitrionNombrePorId.get(a.anfitrionId) || '—') : '—',
            reservasCount,
            comentariosCount
          } as AlojamientoAdminItem;
        });
      })
      .catch(() => {
        this.error = 'No se pudieron cargar los datos de alojamientos.';
      })
      .finally(() => {
        this.cargando = false;
      });
  }

  private agruparReservasPorAlojamiento(reservas: Reserva[]): Map<number, number> {
    const map = new Map<number, number>();
    for (const r of reservas) {
      const actual = map.get(r.alojamientoId) || 0;
      map.set(r.alojamientoId, actual + 1);
    }
    return map;
  }

  private agruparComentariosPorAlojamiento(comentarios: ComentarioResponse[]): Map<number, number> {
    const map = new Map<number, number>();
    for (const c of comentarios) {
      const actual = map.get(c.alojamientoId) || 0;
      map.set(c.alojamientoId, actual + 1);
    }
    return map;
  }

  private mapaAnfitrionNombre(usuarios: UsuarioAdminSummary[]): Map<number, string> {
    const map = new Map<number, string>();
    for (const u of usuarios) {
      map.set(u.id, u.nombre);
    }
    return map;
  }
}
