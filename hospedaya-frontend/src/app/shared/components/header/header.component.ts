import { Component, OnInit, Input } from '@angular/core';
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
export class HeaderComponent implements OnInit {
  @Input() variant: 'default' | 'host' = 'default';

  user: any = null;
  showMenu = false;
  isAnfitrion = false;
  isHuesped = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    if (this.user) {
      this.isAnfitrion = this.user.rol === 'ANFITRION';
      this.isHuesped = this.user.rol === 'HUESPED';
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
