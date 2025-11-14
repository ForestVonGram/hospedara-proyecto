import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { UsuarioProfile, UsuarioService } from '../../../services/usuario.service';

@Component({
  selector: 'app-usuario-detalle-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HeaderComponent],
  templateUrl: './usuario-detalle-admin.component.html',
  styleUrls: ['./usuario-detalle-admin.component.css']
})
export class UsuarioDetalleAdminComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private usuarioService = inject(UsuarioService);

  usuario?: UsuarioProfile;
  cargando = false;
  guardando = false;
  eliminando = false;
  error: string | null = null;
  mensaje: string | null = null;

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (!id || isNaN(id)) {
      this.error = 'Usuario inválido';
      return;
    }

    this.cargarUsuario(id);
  }

  private cargarUsuario(id: number): void {
    this.cargando = true;
    this.error = null;
    this.usuarioService.obtener(id).subscribe({
      next: (u) => {
        this.usuario = u;
      },
      error: () => {
        this.error = 'No se pudo cargar la información del usuario.';
      },
      complete: () => {
        this.cargando = false;
      }
    });
  }

  guardarCambios(): void {
    if (!this.usuario) return;
    this.guardando = true;
    this.error = null;
    this.mensaje = null;

    this.usuarioService.update(this.usuario.id, {
      nombre: this.usuario.nombre,
      email: this.usuario.email,
      telefono: this.usuario.telefono,
      rol: this.usuario.rol,
      activo: this.usuario.activo
    }).subscribe({
      next: (u) => {
        this.usuario = u;
        this.mensaje = 'Cambios guardados correctamente.';
      },
      error: () => {
        this.error = 'No se pudieron guardar los cambios.';
      },
      complete: () => {
        this.guardando = false;
      }
    });
  }

  toggleActivo(): void {
    if (!this.usuario) return;
    const id = this.usuario.id;
    const activar = !this.usuario.activo;
    this.guardando = true;
    this.error = null;
    this.mensaje = null;

    const obs = activar
      ? this.usuarioService.activar(id)
      : this.usuarioService.desactivar(id);

    obs.subscribe({
      next: (u) => {
        this.usuario = u;
        this.mensaje = activar ? 'Usuario activado.' : 'Usuario bloqueado.';
      },
      error: () => {
        this.error = 'No se pudo cambiar el estado del usuario.';
      },
      complete: () => {
        this.guardando = false;
      }
    });
  }

  eliminarUsuario(): void {
    if (!this.usuario) return;
    const confirmar = window.confirm('¿Seguro que deseas eliminar este usuario? Esta acción no se puede deshacer.');
    if (!confirmar) return;

    this.eliminando = true;
    this.error = null;
    this.mensaje = null;

    this.usuarioService.eliminar(this.usuario.id).subscribe({
      next: () => {
        this.mensaje = 'Usuario eliminado correctamente.';
        this.router.navigate(['/admin/usuarios']);
      },
      error: () => {
        this.error = 'No se pudo eliminar el usuario.';
      },
      complete: () => {
        this.eliminando = false;
      }
    });
  }

  volverListado(): void {
    this.router.navigate(['/admin/usuarios']);
  }
}
