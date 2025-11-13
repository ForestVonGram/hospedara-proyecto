import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { Reserva, ReservaService } from '../../services/reserva.service';
import { Alojamiento, AlojamientoService } from '../../services/alojamiento.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { HeaderComponent } from '../../shared/components/header/header.component';

interface ReservaVista extends Reserva {
  alojamiento?: Alojamiento;
}

@Component({
  selector: 'app-reservas',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './reservas.component.html',
  styleUrls: ['./reservas.component.css']
})
export class ReservasComponent implements OnInit {
  user?: UsuarioProfile;
  reservas: ReservaVista[] = [];

  constructor(private usuarioService: UsuarioService, private reservaService: ReservaService, private alojService: AlojamientoService, private router: Router, private auth: AuthService) {}

  ngOnInit(): void {
    const u = this.auth.getUser();
    if (!u) {
      this.router.navigate(['/login']);
      return;
    }
    this.user = u;
    this.cargarReservas();
  }

  cargarReservas() {
    if (!this.user?.id) return;
    this.reservaService.porUsuario(Number(this.user.id)).subscribe((list: Reserva[]) => {
      this.reservas = list.map((r: Reserva) => ({...r}));
      // cargar detalles de alojamiento
      this.reservas.forEach((rv: ReservaVista) => {
        this.alojService.obtener(Number(rv.alojamientoId)).subscribe((a: Alojamiento) => rv.alojamiento = a);
      });
    });
  }

  logout(){ this.auth.logout(); this.router.navigate(['/']); }

  resolverImg(a?: Alojamiento): string {
    if (!a) return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
    const url = a.imagenes && a.imagenes.length ? a.imagenes[0] : '';
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop';
  }
}
