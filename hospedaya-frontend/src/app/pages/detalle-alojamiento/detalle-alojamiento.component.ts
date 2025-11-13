import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, ParamMap, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { DetalleAlojamientoMapComponent } from '../../mapbox/detalle-alojamiento-map.component';
import { ComentarioService, ComentarioResponse } from '../../services/comentario.service';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../shared/components/header/header.component';
@Component({
  selector: 'app-detalle-alojamiento',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent, DetalleAlojamientoMapComponent],
  templateUrl: './detalle-alojamiento.component.html',
  styleUrl: './detalle-alojamiento.component.css'
})
export class DetalleAlojamientoComponent implements OnInit, OnDestroy {
  id?: number;
  alojamiento?: Alojamiento;
  loading = true;
  error?: string;

  // Fechas seleccionadas en el detalle (para mostrar calendario)
  checkIn?: string | null;
  checkOut?: string | null;

  // Comentarios
  comentarios: ComentarioResponse[] = [];
  avgRating = 0;
  newComment = { texto: '', calificacion: 5 };
  posting = false;

  private sub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alojService: AlojamientoService,
    private comentarioService: ComentarioService
  ) {}

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe((params: ParamMap) => {
      const idStr = params.get('id');
      const id = idStr ? Number(idStr) : NaN;
      if (!id || Number.isNaN(id)) {
        this.error = 'Identificador de alojamiento inválido';
        this.loading = false;
        return;
      }
      this.id = id;
      this.cargar(id);
      this.cargarComentarios(id);
    });
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  cargar(id: number) {
    this.loading = true;
    this.error = undefined;
    this.alojService.obtener(id).subscribe({
      next: (a) => {
        // Asegurar tipo numérico para currency pipe
        const precio = Number((a as any).precioPorNoche);
        this.alojamiento = { ...a, precioPorNoche: isNaN(precio) ? 0 : precio } as any;
        this.loading = false;
      },
      error: (e) => { console.error(e); this.error = 'No se pudo cargar el alojamiento'; this.loading = false; }
    });
  }

  cargarComentarios(id: number) {
    this.comentarioService.porAlojamiento(id).subscribe({
      next: (list) => {
        this.comentarios = list || [];
        const vals = this.comentarios.map(c => Number(c.calificacion) || 0);
        const sum = vals.reduce((a,b)=>a+b,0);
        this.avgRating = vals.length ? +(sum / vals.length).toFixed(1) : 0;
      },
      error: () => {
        this.comentarios = [];
        this.avgRating = 0;
      }
    });
  }

  canComment(): boolean {
    // Política simple: cualquier usuario logueado puede comentar.
    // Si deseas, podemos exigir reserva confirmada.
    return true;
  }

  async enviarComentario() {
    if (!this.id || !this.newComment.texto.trim()) return;
    if (this.newComment.calificacion < 1 || this.newComment.calificacion > 5) return;
    const user = (window as any).localStorage ? JSON.parse(localStorage.getItem('user') || 'null') : null;
    if (!user?.id) { this.router.navigate(['/login']); return; }

    this.posting = true;
    this.comentarioService.crear({
      usuarioId: Number(user.id),
      alojamientoId: Number(this.id),
      texto: this.newComment.texto.trim(),
      calificacion: Number(this.newComment.calificacion)
    }).subscribe({
      next: () => {
        this.newComment = { texto: '', calificacion: 5 };
        this.cargarComentarios(Number(this.id));
      },
      error: () => {},
      complete: () => { this.posting = false; }
    });
  }

  resolverImg(url?: string): string {
    if (!url) return 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=1200&auto=format&fit=crop';
    return url.startsWith('http') ? url : `http://localhost:8080${url}`;
  }

  today(): string {
    const d = new Date();
    const m = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${d.getFullYear()}-${m}-${day}`;
  }

  round(n: number): number { return Math.round(n || 0); }

  minCheckout(): string {
    if (!this.checkIn) {
      // mañana
      const d = new Date();
      d.setDate(d.getDate() + 1);
      const m = (d.getMonth() + 1).toString().padStart(2, '0');
      const day = d.getDate().toString().padStart(2, '0');
      return `${d.getFullYear()}-${m}-${day}`;
    }
    const d = new Date(this.checkIn + 'T00:00:00');
    d.setDate(d.getDate() + 1);
    const m = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${d.getFullYear()}-${m}-${day}`;
  }

  onCheckInChange(value: string) {
    this.checkIn = value;
    // Si checkout es anterior a minCheckout, lo limpiamos
    if (this.checkOut && this.checkOut < this.minCheckout()) {
      this.checkOut = null;
    }
  }

}
