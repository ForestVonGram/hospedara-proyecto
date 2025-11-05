import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UsuarioService, UsuarioProfile } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile-setup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profile-setup.component.html',
  styleUrl: './profile-setup.component.css'
})
export class ProfileSetupComponent implements OnInit {
  user?: UsuarioProfile;
  telefono: string = '';
  previewUrl: string | ArrayBuffer | null = null;
  selectedFile?: File;
  saving = false;
  error = '';

  constructor(private usuarioService: UsuarioService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.usuarioService.me().subscribe({
      next: (u) => {
        this.user = u;
        this.telefono = u.telefono || '';
        this.previewUrl = u.fotoPerfilUrl ? (u.fotoPerfilUrl.startsWith('http') ? u.fotoPerfilUrl : 'http://localhost:8080' + u.fotoPerfilUrl) : null;
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

    this.usuarioService.update(this.user.id, { telefono: this.telefono }).subscribe({
      next: () => {
        if (this.selectedFile) {
          this.usuarioService.uploadFoto(this.user!.id, this.selectedFile).subscribe({
            next: () => {
              // refrescar perfil y cachear
              this.usuarioService.me().subscribe({
                next: (u) => this.auth.saveUser(u),
                complete: () => this.router.navigate(['/dashboard'])
              });
            },
            error: () => {
              this.saving = false;
              this.error = 'Error subiendo la foto';
            }
          });
        } else {
          // solo teléfono actualizado, refrescar cache
          this.usuarioService.me().subscribe({
            next: (u) => this.auth.saveUser(u),
            complete: () => this.router.navigate(['/dashboard'])
          });
        }
      },
      error: () => {
        this.saving = false;
        this.error = 'No se pudo guardar tu información';
      }
    });
  }
}
