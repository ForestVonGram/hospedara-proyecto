import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { ImagenService } from '../../services/imagen.service';
import { ImageUploadService } from '../../services/image-upload.service';

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
  email: string = '';
  telefono: string = '';
  previewUrl: string | ArrayBuffer | null = null;
  selectedFile?: File;
  saving = false;
  error = '';

  constructor(
    private usuarioService: UsuarioService,
    private auth: AuthService,
    private router: Router,
    private imagenService: ImagenService,
    private imageUpload: ImageUploadService
  ) {}

  ngOnInit(): void {
    this.usuarioService.me().subscribe({
      next: (u) => {
        this.user = u;
        this.nombre = u.nombre || '';
        this.email = u.email || '';
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

    const doUpdate = (fotoPerfilUrl?: string) => {
      const payload: any = {
        nombre: this.nombre,
        email: this.email,
        telefono: this.telefono
      };
      if (fotoPerfilUrl) {
        payload.fotoPerfilUrl = fotoPerfilUrl; // solo Cloudinary URL, no archivo
      }

      this.usuarioService.update(this.user!.id, payload).subscribe({
        next: () => {
          // Refrescar perfil desde el backend
          this.usuarioService.me().subscribe({
            next: (u) => {
              this.auth.saveUser(u);
              const dashboardRoute = u.rol === 'ANFITRION' ? '/dashboard-anfitrion' : '/dashboard';
              this.router.navigate([dashboardRoute]);
            },
            error: () => {
              const dashboardRoute = this.user?.rol === 'ANFITRION' ? '/dashboard-anfitrion' : '/dashboard';
              this.router.navigate([dashboardRoute]);
            },
            complete: () => {
              this.saving = false;
            }
          });
        },
        error: (err) => {
          this.saving = false;
          this.error = err?.error?.message || 'No se pudo guardar tu información';
        }
      });
    };

    // Si no hay archivo seleccionado, solo actualizar datos básicos
    if (!this.selectedFile) {
      doUpdate();
      return;
    }

    // Validar archivo antes de subir a Cloudinary
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

    // Subir a Cloudinary usando el servicio genérico y usar SOLO la URL devuelta
    this.imageUpload.uploadImage(this.selectedFile, 'perfiles').subscribe({
      next: (result) => {
        const url = result.url;
        this.previewUrl = url;
        if (this.user) this.user.fotoPerfilUrl = url;
        doUpdate(url);
      },
      error: (err) => {
        this.saving = false;
        this.error = err?.error?.message || 'Error al subir la imagen a la nube';
      }
    });
  }
}
