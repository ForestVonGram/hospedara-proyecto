import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { UsuarioAdminSummary, UsuarioService } from '../../../services/usuario.service';
import { Reserva, ReservaService } from '../../../services/reserva.service';
import { ComentarioResponse, ComentarioService } from '../../../services/comentario.service';

interface UsuarioEstadisticas extends UsuarioAdminSummary {
  reservasCount: number;
  reservasDetalle: { alojamientoId: number; alojamientoNombre?: string }[];
  comentariosCount: number;
  comentariosDetalle: { alojamientoId: number; alojamientoNombre?: string }[];
}

@Component({
  selector: 'app-usuarios-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent, FormsModule],
  templateUrl: './usuarios-admin.component.html',
  styleUrls: ['./usuarios-admin.component.css']
})
export class UsuariosAdminComponent implements OnInit {
  usuarios: UsuarioEstadisticas[] = [];
  cargando = false;
  error: string | null = null;
  filtro = '';

  constructor(
    private usuarioService: UsuarioService,
    private reservaService: ReservaService,
    private comentarioService: ComentarioService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  get usuariosFiltrados(): UsuarioEstadisticas[] {
    const term = this.filtro.trim().toLowerCase();
    if (!term) return this.usuarios;
    return this.usuarios.filter(u =>
      u.nombre.toLowerCase().includes(term) ||
      u.email.toLowerCase().includes(term)
    );
  }

  private cargarDatos(): void {
    this.cargando = true;
    this.error = null;

    // Cargar usuarios, reservas y comentarios en paralelo
    Promise.all([
      this.usuarioService.listarTodos().toPromise(),
      this.reservaService.listarTodas().toPromise(),
      this.comentarioService.listarTodos().toPromise()
    ])
      .then(([usuarios, reservas, comentarios]) => {
        const reservasPorUsuario = this.agruparReservasPorUsuario(reservas || []);
        const comentariosPorUsuario = this.agruparComentariosPorUsuario(comentarios || []);

        this.usuarios = (usuarios || []).map(u => {
          const rInfo = reservasPorUsuario.get(u.id) || { count: 0, items: [] };
          const cInfo = comentariosPorUsuario.get(u.id) || { count: 0, items: [] };

          return {
            ...u,
            reservasCount: rInfo.count,
            reservasDetalle: rInfo.items,
            comentariosCount: cInfo.count,
            comentariosDetalle: cInfo.items,
          } as UsuarioEstadisticas;
        });
      })
      .catch(() => {
        this.error = 'No se pudieron cargar los datos de usuarios.';
      })
      .finally(() => {
        this.cargando = false;
      });
  }

  private agruparReservasPorUsuario(reservas: Reserva[]): Map<number, { count: number; items: { alojamientoId: number; alojamientoNombre?: string }[] }> {
    const map = new Map<number, { count: number; items: { alojamientoId: number; alojamientoNombre?: string }[] }>();
    for (const r of reservas) {
      const entry = map.get(r.usuarioId) || { count: 0, items: [] };
      entry.count += 1;
      entry.items.push({ alojamientoId: r.alojamientoId });
      map.set(r.usuarioId, entry);
    }
    return map;
  }

  private agruparComentariosPorUsuario(comentarios: ComentarioResponse[]): Map<number, { count: number; items: { alojamientoId: number; alojamientoNombre?: string }[] }> {
    const map = new Map<number, { count: number; items: { alojamientoId: number; alojamientoNombre?: string }[] }>();
    for (const c of comentarios) {
      const entry = map.get(c.usuarioId) || { count: 0, items: [] };
      entry.count += 1;
      entry.items.push({ alojamientoId: c.alojamientoId, alojamientoNombre: c.alojamientoNombre });
      map.set(c.usuarioId, entry);
    }
    return map;
  }

  avatarUrl(u: UsuarioAdminSummary): string | null {
    const url = u.fotoPerfilUrl;
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : null;
  }
}
