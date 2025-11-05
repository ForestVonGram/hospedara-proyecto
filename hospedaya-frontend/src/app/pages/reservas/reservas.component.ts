import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { Reserva, ReservaService } from '../../services/reserva.service';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface ReservaVista extends Reserva {
  alojamiento?: Alojamiento;
}

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './reservas.component.html',
  styleUrl: './reservas.component.css'
})
export class ReservasComponent implements OnInit {
  user?: UsuarioProfile;
  reservas: ReservaVista[] = [];

  constructor(private usuarioService: UsuarioService, private reservaService: ReservaService, private alojService: AlojamientoService, private router: Router, private auth: AuthService) {}

  ngOnInit(): void {
    this.user = this.auth.getUser();
    if (this.user) this.cargarReservas();
    this.usuarioService.me().subscribe({
      next: u => { this.user = u; this.cargarReservas(); },
      error: () => { this.user = undefined; this.reservas = []; }
    });
  }

  cargarReservas() {
    if (!this.user?.id) return;
    this.reservaService.porUsuario(this.user.id).subscribe(list => {
      this.reservas = list.map(r => ({...r}));
      // cargar detalles de alojamiento
      this.reservas.forEach(rv => {
        this.alojService.obtener(rv.alojamientoId).subscribe(a => rv.alojamiento = a);
      });
    });
  }

  showMenu = false;
  avatar(): string | null {
    const u = this.user?.fotoPerfilUrl;
    return u ? (u.startsWith('http') ? u : `http://localhost:8080${u}`) : null;
  }
  toggleMenu(){ this.showMenu = !this.showMenu; }
  logout(){ this.auth.logout(); this.router.navigate(['/']); }

  resolverImg(a?: Alojamiento): string {
    if (!a) return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
    const url = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
  }
}