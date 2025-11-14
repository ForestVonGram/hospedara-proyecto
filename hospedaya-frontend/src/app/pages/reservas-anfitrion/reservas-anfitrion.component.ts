import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService, Usuario } from '../../services/auth.service';
import { AlojamientoResponseDTO, AlojamientoService } from '../../services/alojamiento.service';
import { Reserva, ReservaService } from '../../services/reserva.service';
import { UsuarioProfile, UsuarioService } from '../../services/usuario.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {HeaderComponent} from '../../shared/components/header/header.component';

interface ReservaHostVista extends Reserva {
  alojamiento?: AlojamientoResponseDTO;
  huesped?: UsuarioProfile;
}

@Component({
  selector: 'app-reservas-anfitrion',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './reservas-anfitrion.component.html',
  styleUrls: ['./reservas-anfitrion.component.css']
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
    private reservaService: ReservaService,
    private usuarioService: UsuarioService
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

            if (!propias.length) {
              this.reservas = [];
              this.cargando = false;
              return;
            }

            // IDs únicos de huéspedes
            const usuarioIds = Array.from(new Set(propias
              .map(r => Number(r.usuarioId))
              .filter(id => !!id)
            ));

            // Si por alguna razón no hay IDs de usuario, al menos mostramos las reservas
            if (!usuarioIds.length) {
              this.reservas = propias.map(r => ({
                ...r,
                alojamiento: alojMap.get(Number(r.alojamientoId))
              }));
              this.cargando = false;
              return;
            }

            // 3) Cargar los perfiles de los huéspedes
            forkJoin(usuarioIds.map(id =>
              this.usuarioService.obtener(id).pipe(
                // Si falla un usuario en particular, devolvemos null para no romper todo
                catchError(() => of(null))
              )
            )).subscribe({
              next: (perfiles) => {
                const mapaUsuarios = new Map<number, UsuarioProfile>();
                perfiles.forEach((u, index) => {
                  const id = usuarioIds[index];
                  if (u && id) {
                    mapaUsuarios.set(id, u as UsuarioProfile);
                  }
                });

                this.reservas = propias.map(r => ({
                  ...r,
                  alojamiento: alojMap.get(Number(r.alojamientoId)),
                  huesped: mapaUsuarios.get(Number(r.usuarioId))
                }));
              },
              error: (err) => {
                // Si falla la carga de usuarios, mostramos reservas sin datos de huésped
                this.error = this.extraerMensajeError(err, 'No se pudieron cargar los datos de los huéspedes.');
                this.reservas = propias.map(r => ({
                  ...r,
                  alojamiento: alojMap.get(Number(r.alojamientoId))
                }));
              },
              complete: () => {
                this.cargando = false;
              }
            });
          },
          error: (err) => {
            this.error = this.extraerMensajeError(err, 'No se pudieron cargar las reservas.');
            this.cargando = false;
          }
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

  private fechaYMDaDate(ymd?: string): Date | null {
    if (!ymd) return null;
    const d = new Date(ymd + 'T00:00:00');
    d.setHours(0,0,0,0);
    return isNaN(d.getTime()) ? null : d;
  }

  estadoVisual(r: Reserva): string {
    const estado = (r.estado || '').toUpperCase();
    const hoy = new Date(); hoy.setHours(0,0,0,0);
    const ini = this.fechaYMDaDate(r.fechaInicio);
    const fin = this.fechaYMDaDate(r.fechaFin);

    if (estado === 'PAGADA' && ini && fin) {
      if (ini <= hoy && hoy <= fin) return 'EN CURSO';
      if (fin < hoy) return 'TERMINADA';
    }
    return r.estado || 'PENDIENTE';
  }

  estadoClase(estado?: string): string {
    const e = (estado || '').toUpperCase();
    if (e === 'EN CURSO') return 'en-curso';
    if (e === 'TERMINADA') return 'terminada';
    if (e === 'CONFIRMADA') return 'confirmada';
    if (e === 'CANCELADA') return 'cancelada';
    return 'pendiente';
  }

  private extraerMensajeError(err: any, generico: string): string {
    if (typeof err?.error === 'string' && err.error.trim().length > 0) return err.error;
    if (err?.error?.message) return err.error.message;
    return generico;
  }
}
