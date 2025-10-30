import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { AlojamientoService } from '../../services/alojamiento.service';
import { AlojamientoResponseDTO } from '../../../../core/services/api/alojamiento-api.service';
import { AlojamientoCardComponent } from '../../components/alojamiento-card/alojamiento-card.component';

@Component({
  selector: 'app-alojamientos-list-page',
  standalone: true,
  imports: [CommonModule, RouterModule, AlojamientoCardComponent],
  templateUrl: './alojamientos-list.page.html',
  styleUrls: ['./alojamientos-list.page.scss'],
})
export class AlojamientosListPage {
  alojamientos$!: Observable<AlojamientoResponseDTO[]>;

  constructor(public alojamientoService: AlojamientoService) {
    this.alojamientos$ = this.alojamientoService.getAll();
  }
}
