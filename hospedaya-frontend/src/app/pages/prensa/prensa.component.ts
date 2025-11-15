import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/components/header/header.component';
import { Noticia, NoticiaService } from '../../services/noticia.service';

@Component({
  selector: 'app-prensa',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './prensa.component.html',
  styleUrls: ['./prensa.component.css']
})
export class PrensaComponent implements OnInit {
  destacada: Noticia | null = null;
  noticias: Noticia[] = [];
  cargando = false;
  error: string | null = null;

  constructor(private noticiaService: NoticiaService) {}

  ngOnInit(): void {
    this.cargando = true;
    this.noticiaService.listarPublicas().subscribe({
      next: data => {
        const list = data || [];
        this.destacada = list.length ? list[0] : null;
        // En el historial también debe aparecer la destacada, por eso usamos la lista completa
        this.noticias = list;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las noticias de prensa.';
        this.cargando = false;
      }
    });
  }
}
