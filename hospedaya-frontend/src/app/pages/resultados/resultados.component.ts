import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { ImagenAlojamientoService } from '../../services/imagen-alojamiento.service';

@Component({
  selector: 'app-resultados',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './resultados.component.html',
  styleUrl: './resultados.component.css'
})
export class ResultadosComponent implements OnInit {
  user?: UsuarioProfile;
  destino = '';
  checkin = '';
  checkout = '';
  huespedes = 1;
  alojamientos: Alojamiento[] = [];
  filtrados: Alojamiento[] = [];

  // Filtros de precio (COP). Dejar vacío = Sin límite
  minPrecio: number | null = null;
  maxPrecio: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private alojService: AlojamientoService,
    private usuarioService: UsuarioService,
    private router: Router,
    private auth: AuthService,
    private imagenService: ImagenAlojamientoService
  ) {}

  ngOnInit(): void {
    this.user = this.auth.getUser();
    this.usuarioService.me().subscribe({ next: (u) => this.user = u, error: () => this.user = undefined });

    this.route.queryParamMap.subscribe(params => {
      this.destino = params.get('destino') || '';
      this.checkin = params.get('checkin') || '';
      this.checkout = params.get('checkout') || '';
      this.huespedes = +(params.get('huespedes') || '1');
      this.cargar();
    });
  }

  cargar() {
    this.alojService.listar().subscribe((list: Alojamiento[]) => {
      // Asegurar que el precio sea numérico para mostrar con currency pipe
      this.alojamientos = (list || []).map(a => ({
        ...a,
        precioPorNoche: Number(a.precioPorNoche),
        imagenes: Array.isArray(a.imagenes) ? a.imagenes : []
      } as Alojamiento));

      // Intentar hidratar previews con imágenes reales si vienen vacías
      for (const a of this.alojamientos) {
        if ((!a.imagenes || a.imagenes.length === 0) && a.id) {
          this.imagenService.listarPorAlojamiento(Number(a.id)).subscribe({
            next: (imgs) => {
              const urls = (imgs || []).map(i => i.url);
              if (urls.length > 0) {
                a.imagenes = urls;
              }
            }
          });
        }
      }

      // Mantener sin límite por defecto
      this.minPrecio = null;
      this.maxPrecio = null;
      this.aplicarFiltros();
    });
  }

  aplicarFiltros() {
    const d = this.destino.toLowerCase();
    const min = this.minPrecio == null || this.minPrecio === undefined || this.minPrecio === 0 ? undefined : Number(this.minPrecio);
    const max = this.maxPrecio == null || this.maxPrecio === undefined || this.maxPrecio === 0 ? undefined : Number(this.maxPrecio);

    this.filtrados = this.alojamientos.filter((a: Alojamiento) => {
      const matchDestino = d ? (a.direccion?.toLowerCase().includes(d) || a.titulo?.toLowerCase().includes(d)) : true;
      const precio = Number(a.precioPorNoche);
      const okMin = (min === undefined) ? true : (!Number.isNaN(precio) && precio >= min);
      const okMax = (max === undefined) ? true : (!Number.isNaN(precio) && precio <= max);
      // Si ambos están definidos y min > max, no filtra por rango (o podríamos intercambiar). Aquí normalizamos: si min>max, intercambiamos.
      return matchDestino && okMin && okMax;
    });
  }

  resolverImg(a: Alojamiento): string {
    const url = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=1200&auto=format&fit=crop';
  }

  logout(){ this.auth.logout(); this.router.navigate(['/']); }
}
