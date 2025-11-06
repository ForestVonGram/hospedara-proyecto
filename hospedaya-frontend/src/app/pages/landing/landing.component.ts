import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  protected readonly year = new Date().getFullYear();
  user?: UsuarioProfile;
  showMenu = false;

  constructor(private usuarioService: UsuarioService, private auth: AuthService, private router: Router) {}

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

  avatarUrl(): string | null {
    const u = this.user?.fotoPerfilUrl;
    return u ? (u.startsWith('http') ? u : `http://localhost:8080${u}`) : null;
  }
  toggleMenu(){ this.showMenu = !this.showMenu; }
  logout(){ this.auth.logout(); this.router.navigate(['/']); }
}
