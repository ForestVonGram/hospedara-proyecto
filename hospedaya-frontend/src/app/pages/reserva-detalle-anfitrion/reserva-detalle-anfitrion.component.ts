import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Reserva, ReservaService } from '../../services/reserva.service';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { UsuarioProfile, UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-reserva-detalle-anfitrion',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './reserva-detalle-anfitrion.component.html',
  styleUrls: ['./reserva-detalle-anfitrion.component.css']
})
export class ReservaDetalleAnfitrionComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private reservaService = inject(ReservaService);
  private alojamientoService = inject(AlojamientoService);
  private usuarioService = inject(UsuarioService);

  reserva?: Reserva;
  alojamiento?: Alojamiento;
  huesped?: UsuarioProfile;
  cargando = false;
  error = '';

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (!id || isNaN(id)) { this.error = 'Reserva inválida'; return; }

    this.cargando = true;
    this.reservaService.obtener(id).subscribe({
      next: (r) => {
        this.reserva = r;

        // Cargar alojamiento
        const aloId = Number(r.alojamientoId);
        if (aloId) {
          this.alojamientoService.obtener(aloId).subscribe(a => this.alojamiento = a);
        }

        // Cargar datos del huésped
        const userId = Number(r.usuarioId);
        if (userId) {
          this.usuarioService.obtener(userId).subscribe({
            next: (u) => this.huesped = u,
            error: () => {
              // si falla, simplemente dejamos el ID
            }
          });
        }
      },
      error: () => this.error = 'No se pudo cargar la reserva',
      complete: () => this.cargando = false
    });
  }

  estadoVisual(): string {
    const r = this.reserva; if (!r) return '';
    const estado = (r.estado || '').toUpperCase();
    const hoy = new Date(); hoy.setHours(0,0,0,0);
    const ini = new Date(r.fechaInicio + 'T00:00:00'); ini.setHours(0,0,0,0);
    const fin = new Date(r.fechaFin + 'T00:00:00'); fin.setHours(0,0,0,0);
    if (estado === 'PAGADA') {
      if (ini <= hoy && hoy <= fin) return 'EN CURSO';
      if (fin < hoy) return 'TERMINADA';
    }
    return r.estado || 'PENDIENTE';
  }

  totalNoches(): number {
    if (!this.reserva) return 0;
    const i = new Date(this.reserva.fechaInicio + 'T00:00:00');
    const f = new Date(this.reserva.fechaFin + 'T00:00:00');
    const ms = f.getTime() - i.getTime();
    const d = Math.ceil(ms / (1000*60*60*24));
    return d > 0 ? d : 0;
  }

  totalEstimado(): number {
    const noches = this.totalNoches();
    const precio = Number(this.alojamiento?.precioPorNoche || 0);
    return noches * precio;
  }

  imagen(): string {
    const a = this.alojamiento;
    if (!a) return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
    const url = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
  }

  irAlojamiento() {
    if (this.alojamiento?.id) this.router.navigate(['/alojamientos/gestion']);
  }
}
