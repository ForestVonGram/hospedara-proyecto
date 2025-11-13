import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { DetalleAlojamientoMapComponent } from '../../mapbox/detalle-alojamiento-map.component';

@Component({
  selector: 'app-detalle-alojamiento',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DetalleAlojamientoMapComponent],
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

  private sub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alojService: AlojamientoService
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
    });
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  cargar(id: number) {
    this.loading = true;
    this.error = undefined;
    this.alojService.obtener(id).subscribe({
      next: (a) => {
        this.alojamiento = a;
        this.loading = false;
      },
      error: (e) => { console.error(e); this.error = 'No se pudo cargar el alojamiento'; this.loading = false; }
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
