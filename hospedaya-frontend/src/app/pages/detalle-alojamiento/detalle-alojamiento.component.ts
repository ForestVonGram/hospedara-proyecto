import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, ParamMap, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { DetalleAlojamientoMapComponent } from '../../mapbox/detalle-alojamiento-map.component';

@Component({
  selector: 'app-detalle-alojamiento',
  standalone: true,
  imports: [CommonModule, RouterModule, DetalleAlojamientoMapComponent],
  templateUrl: './detalle-alojamiento.component.html',
  styleUrl: './detalle-alojamiento.component.css'
})
export class DetalleAlojamientoComponent implements OnInit, OnDestroy {
  id?: number;
  alojamiento?: Alojamiento;
  loading = true;
  error?: string;


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
}
