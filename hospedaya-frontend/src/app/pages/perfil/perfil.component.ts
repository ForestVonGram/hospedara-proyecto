import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService, Usuario } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent {
  user?: Usuario;
  loading = true;
  error = '';

  constructor(private auth: AuthService) {}

  ngOnInit() {
    this.loadProfile();
  }

  private loadProfile() {
    this.loading = true;
    this.error = '';
    this.auth.getMe().subscribe({
      next: (user) => {
        this.user = user;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error ?? 'No se pudo cargar el perfil.';
        this.loading = false;
      }
    });
  }

  get initials(): string {
    if (!this.user?.nombre) return '';
    return this.user.nombre
      .split(' ')
      .map(p => p[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }
}
