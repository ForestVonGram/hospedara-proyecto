import { Component, OnDestroy, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, ParamMap, Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { MapService } from '../../mapbox/map-service';

@Component({
  selector: 'app-detalle-alojamiento',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './detalle-alojamiento.component.html',
  styleUrl: './detalle-alojamiento.component.css'
})
export class DetalleAlojamientoComponent implements OnInit, OnDestroy, AfterViewInit {
  id?: number;
  alojamiento?: Alojamiento;
  loading = true;
  error?: string;


  private sub?: Subscription;

  private mapInitialized = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private alojService: AlojamientoService,
    private mapService: MapService
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

  ngAfterViewInit(): void {
    // El mapa se inicializará cuando se cargue el alojamiento
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  cargar(id: number) {
    this.loading = true;
    this.error = undefined;
    this.alojService.obtener(id).subscribe({
      next: (a) => {
        this.alojamiento = a;
        this.loading = false;
        // Inicializar el mapa si el alojamiento tiene coordenadas
        if (a.latitud != null && a.longitud != null) {
          this.initializeMap();
        }
      },
      error: (e) => { console.error(e); this.error = 'No se pudo cargar el alojamiento'; this.loading = false; }
    });
  }

  private initializeMap(): void {
    if (this.mapInitialized || !this.alojamiento) return;
    
    setTimeout(() => {
      this.mapService.create('map-detalle');
      if (this.alojamiento?.latitud != null && this.alojamiento?.longitud != null) {
        this.mapService.setMarker(this.alojamiento.longitud, this.alojamiento.latitud);
      }
      this.mapInitialized = true;
    }, 100);
  }

  resolverImg(url?: string): string {
    if (!url) return 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=1200&auto=format&fit=crop';
    return url.startsWith('http') ? url : `http://localhost:8080${url}`;
  }
}
