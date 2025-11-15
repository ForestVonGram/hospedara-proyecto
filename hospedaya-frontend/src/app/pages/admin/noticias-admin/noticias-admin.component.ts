import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../../shared/components/header/header.component';
import { Noticia, NoticiaService } from '../../../services/noticia.service';

@Component({
  selector: 'app-noticias-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HeaderComponent],
  templateUrl: './noticias-admin.component.html',
  styleUrls: ['./noticias-admin.component.css']
})
export class NoticiasAdminComponent implements OnInit {
  noticias: Noticia[] = [];
  cargando = false;
  error: string | null = null;

  mostrarFormulario = false;
  creando = false;
  nueva: Partial<Noticia> = {
    titulo: '',
    resumen: '',
    contenido: ''
  };

  constructor(private noticiaService: NoticiaService) {}

  ngOnInit(): void {
    this.cargarNoticias();
  }

  cargarNoticias(): void {
    this.cargando = true;
    this.error = null;
    this.noticiaService.listarTodas().subscribe({
      next: data => {
        this.noticias = data || [];
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las noticias.';
        this.cargando = false;
      }
    });
  }

  crearNoticia(): void {
    if (!this.nueva.titulo || !this.nueva.contenido) {
      this.error = 'El título y el contenido son obligatorios.';
      return;
    }

    this.error = null;
    this.creando = true;

    const payload: Omit<Noticia, 'id' | 'fechaCreacion'> = {
      titulo: this.nueva.titulo!,
      resumen: this.nueva.resumen || '',
      contenido: this.nueva.contenido!
    };

    this.noticiaService.crear(payload).subscribe({
      next: noticia => {
        this.noticias = [noticia, ...this.noticias];
        this.nueva = { titulo: '', resumen: '', contenido: '' };
        this.mostrarFormulario = false;
        this.creando = false;
      },
      error: () => {
        this.error = 'No se pudo crear la noticia.';
        this.creando = false;
      }
    });
  }
}
