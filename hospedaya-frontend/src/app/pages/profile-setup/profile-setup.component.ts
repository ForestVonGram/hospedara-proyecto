import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { ImagenService } from '../../services/imagen.service';

@Component({
  selector: 'app-profile-setup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profile-setup.component.html',
  styleUrls: ['./profile-setup.component.css']
})
export class ProfileSetupComponent implements OnInit {
  user?: UsuarioProfile;
  nombre: string = '';
  telefono: string = '';
  previewUrl: string | ArrayBuffer | null = null;
  selectedFile?: File;
  saving = false;
  error = '';

  constructor(
    private usuarioService: UsuarioService,
    private auth: AuthService,
    private router: Router,
    private imagenService: ImagenService
  ) {}

  ngOnInit(): void {
    this.usuarioService.me().subscribe({
      next: (u) => {
        this.user = u;
        this.nombre = u.nombre || '';
        this.telefono = u.telefono || '';
        const url = u.fotoPerfilUrl || '';
        this.previewUrl = url
          ? (url.startsWith('http') ? url : `http://localhost:8080${url}`)
          : null;
      },
      error: () => {
        this.error = 'No se pudo cargar tu perfil';
      }
    });
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => (this.previewUrl = reader.result);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  async guardar() {
    if (!this.user) return;
    this.saving = true;
    this.error = '';

    // Primero actualizar nombre y teléfono
    this.usuarioService.update(this.user.id, { nombre: this.nombre, telefono: this.telefono }).subscribe({
      next: () => {
        if (this.selectedFile) {
          // Validar archivo antes de subir
          if (!this.imagenService.isValidImageFile(this.selectedFile)) {
            this.error = 'El archivo debe ser una imagen (JPG, PNG, GIF, WEBP)';
            this.saving = false;
            return;
          }

          if (!this.imagenService.isValidImageSize(this.selectedFile, 5)) {
            this.error = 'La imagen no puede superar 5MB';
            this.saving = false;
            return;
          }

          // Subir imagen a Cloudinary
          this.imagenService.uploadAvatar(this.selectedFile).subscribe({
            next: (response) => {
              // Actualizar vista y cache con la URL de Cloudinary
              this.previewUrl = response.url;
              if (this.user) this.user.fotoPerfilUrl = response.url;

              // Refrescar perfil desde el backend
              this.usuarioService.me().subscribe({
                next: (u) => {
                  this.auth.saveUser(u);
                  // Redirigir según el rol
                  const dashboardRoute = u.rol === 'ANFITRION' ? '/dashboard-anfitrion' : '/dashboard';
                  this.router.navigate([dashboardRoute]);
                },
                error: () => {
                  // Aunque falle el refresh, la imagen ya se subió
                  const dashboardRoute = this.user?.rol === 'ANFITRION' ? '/dashboard-anfitrion' : '/dashboard';
                  this.router.navigate([dashboardRoute]);
                }
              });
            },
            error: (err) => {
              this.saving = false;
              this.error = err.error?.message || 'Error al subir la imagen';
            }
          });
        } else {
          // Solo se actualizó nombre/teléfono, refrescar cache
          this.usuarioService.me().subscribe({
            next: (u) => {
              this.auth.saveUser(u);
              // Redirigir según el rol
              const dashboardRoute = u.rol === 'ANFITRION' ? '/dashboard-anfitrion' : '/dashboard';
              this.router.navigate([dashboardRoute]);
            },
            error: () => {
              const dashboardRoute = this.user?.rol === 'ANFITRION' ? '/dashboard-anfitrion' : '/dashboard';
              this.router.navigate([dashboardRoute]);
            }
          });
        }
      },
      error: (err) => {
        this.saving = false;
        this.error = err?.error?.message || 'No se pudo guardar tu información';
      }
    });
  }
}
