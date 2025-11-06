import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-resultados',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
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
  precioMax?: number;

  constructor(private route: ActivatedRoute, private alojService: AlojamientoService, private usuarioService: UsuarioService, private router: Router, private auth: AuthService) {}

  ngOnInit(): void {
    this.user = this.auth.getUser();
    this.usuarioService.me().subscribe({ next: u => this.user = u, error: () => this.user = undefined });

    this.route.queryParamMap.subscribe(params => {
      this.destino = params.get('destino') || '';
      this.checkin = params.get('checkin') || '';
      this.checkout = params.get('checkout') || '';
      this.huespedes = +(params.get('huespedes') || '1');
      this.cargar();
    });
  }

  cargar() {
    this.alojService.listar().subscribe(list => {
      this.alojamientos = list;
      this.aplicarFiltros();
    });
  }

  aplicarFiltros() {
    const d = this.destino.toLowerCase();
    this.filtrados = this.alojamientos.filter(a => {
      const matchDestino = d ? (a.direccion?.toLowerCase().includes(d) || a.titulo?.toLowerCase().includes(d)) : true;
      const matchPrecio = this.precioMax ? (Number(a.precioPorNoche) <= this.precioMax) : true;
      return matchDestino && matchPrecio;
    });
  }

  resolverImg(a: Alojamiento): string {
    const url = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=1200&auto=format&fit=crop';
  }

  showMenu = false;
  avatar(): string | null {
    const u = this.user?.fotoPerfilUrl;
    return u ? (u.startsWith('http') ? u : `http://localhost:8080${u}`) : null;
  }
  toggleMenu(){ this.showMenu = !this.showMenu; }
  logout(){ this.auth.logout(); this.router.navigate(['/']); }
}