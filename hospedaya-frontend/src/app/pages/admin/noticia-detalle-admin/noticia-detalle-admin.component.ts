import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { Noticia, NoticiaService } from '../../../services/noticia.service';

@Component({
  selector: 'app-noticia-detalle-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HeaderComponent],
  templateUrl: './noticia-detalle-admin.component.html',
  styleUrls: ['./noticia-detalle-admin.component.css']
})
export class NoticiaDetalleAdminComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private noticiaService = inject(NoticiaService);

  noticia?: Noticia;
  cargando = false;
  guardando = false;
  eliminando = false;
  error: string | null = null;
  mensaje: string | null = null;

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (!id || isNaN(id)) {
      this.error = 'Noticia inválida';
      return;
    }

    this.cargarNoticia(id);
  }

  private cargarNoticia(id: number): void {
    this.cargando = true;
    this.error = null;
    this.noticiaService.obtener(id).subscribe({
      next: (n) => {
        this.noticia = n;
      },
      error: () => {
        this.error = 'No se pudo cargar la noticia.';
      },
      complete: () => {
        this.cargando = false;
      }
    });
  }

  guardarCambios(): void {
    if (!this.noticia) return;

    this.guardando = true;
    this.error = null;
    this.mensaje = null;

    const payload = {
      titulo: this.noticia.titulo,
      resumen: this.noticia.resumen,
      contenido: this.noticia.contenido
    };

    this.noticiaService.actualizar(this.noticia.id, payload).subscribe({
      next: (n) => {
        this.noticia = n;
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

  eliminarNoticia(): void {
    if (!this.noticia || this.eliminando) return;
    const ok = window.confirm('¿Seguro que deseas eliminar esta noticia? Esta acción no se puede deshacer.');
    if (!ok) return;

    this.eliminando = true;
    this.error = null;
    this.mensaje = null;

    this.noticiaService.eliminar(this.noticia.id).subscribe({
      next: () => {
        this.mensaje = 'Noticia eliminada correctamente.';
        this.router.navigate(['/admin/noticias']);
      },
      error: () => {
        this.error = 'No se pudo eliminar la noticia.';
        this.eliminando = false;
      },
      complete: () => {
        this.eliminando = false;
      }
    });
  }

  volverListado(): void {
    this.router.navigate(['/admin/noticias']);
  }
}
