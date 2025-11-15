import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import {HeaderComponent} from '../../shared/components/header/header.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HeaderComponent],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent {
  protected readonly year = new Date().getFullYear();

  // Campos de búsqueda
  destino = '';
  checkin = '';
  checkout = '';
  huespedes: number | null = null;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    // Restringe landing si ya está logueado
    const u = this.auth.getUser();
    if (u) {
      this.router.navigate(['/dashboard']);
      return;
    }
    // Si quisieras mostrar algo del usuario en landing (no recomendado), podrías llamar a me() aquí.
  }

  ngAfterViewInit() {
    const cdn = 'https://cdn.jsdelivr.net/npm/flatpickr';
    if (!(window as any).flatpickr) {
      const s = document.createElement('script');
      s.src = cdn;
      s.async = true;
      s.onload = () => this.initDatepickers();
      document.head.appendChild(s);
    } else {
      this.initDatepickers();
    }
  }

  private initDatepickers() {
    try {
      (window as any).flatpickr?.('#checkin', { dateFormat: 'Y-m-d' });
      (window as any).flatpickr?.('#checkout', { dateFormat: 'Y-m-d' });
    } catch {}
  }

  onSearchSubmit() {
    this.router.navigate(['/resultados'], {
      queryParams: {
        destino: this.destino,
        checkin: this.checkin,
        checkout: this.checkout,
        huespedes: this.huespedes || undefined
      }
    });
  }
}
