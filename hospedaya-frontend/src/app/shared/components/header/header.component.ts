import { Component, OnInit, DoCheck, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, DoCheck {
  @Input() variant: 'default' | 'host' = 'default';

  user: any = null;
  showMenu = false;
  isAnfitrion = false;
  isHuesped = false;
  isAdmin = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.syncUserFromAuth();
  }

  ngDoCheck(): void {
    // Re-sincroniza el usuario en cada ciclo de detección de cambios por si cambió tras el login
    this.syncUserFromAuth();
  }

  private syncUserFromAuth(): void {
    const current = this.authService.getUser();
    if (!current && !this.user) {
      return;
    }
    if (!this.user || current?.id !== this.user.id || current?.rol !== this.user.rol) {
      this.user = current;
      this.isAnfitrion = this.user?.rol === 'ANFITRION';
      this.isHuesped = this.user?.rol === 'HUESPED';
      this.isAdmin = this.user?.rol === 'ADMIN';
    }
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  toggleMenu(): void {
    this.showMenu = !this.showMenu;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  avatarUrl(): string | null {
    const url = this.user?.fotoPerfilUrl;
    return url ? (url.startsWith('http') ? url : `http://localhost:8080${url}`) : null;
  }

  getInitials(): string {
    if (!this.user?.nombre) return 'U';
    const names = this.user.nombre.split(' ');
    if (names.length >= 2) {
      return names[0][0] + names[1][0];
    }
    return names[0][0];
  }
}
