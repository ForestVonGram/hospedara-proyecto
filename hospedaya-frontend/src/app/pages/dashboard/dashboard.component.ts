import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  user?: UsuarioProfile;

  destino = '';
  checkin = '';
  checkout = '';
  huespedes = 1;

  destacados = [
    { titulo: 'Casa en la playa', ciudad: 'Cancún, México', precio: 120, rating: 4.8, img: 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?q=80&w=1200&auto=format&fit=crop' },
    { titulo: 'Departamento céntrico', ciudad: 'CDMX, México', precio: 85, rating: 4.5, img: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=1200&auto=format&fit=crop' },
    { titulo: 'Cabaña en el bosque', ciudad: 'Valle de Bravo, México', precio: 95, rating: 4.9, img: 'https://images.unsplash.com/photo-1542718610-a1d656d1884f?q=80&w=1200&auto=format&fit=crop' },
    { titulo: 'Loft moderno', ciudad: 'Guadalajara, México', precio: 75, rating: 4.7, img: 'https://images.unsplash.com/photo-1494526585095-c41746248156?q=80&w=1200&auto=format&fit=crop' }
  ];

  constructor(private usuarioService: UsuarioService, private router: Router) {}

  ngOnInit(): void {
    this.usuarioService.me().subscribe({ next: (u) => (this.user = u) });
  }

  buscar() {
    this.router.navigate(['/buscar'], {
      queryParams: {
        destino: this.destino,
        checkin: this.checkin,
        checkout: this.checkout,
        huespedes: this.huespedes
      }
    });
  }
}
