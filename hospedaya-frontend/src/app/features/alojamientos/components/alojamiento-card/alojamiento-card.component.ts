import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AlojamientoResponseDTO } from '../../../../core/services/api/alojamiento-api.service';

@Component({
  selector: 'app-alojamiento-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './alojamiento-card.component.html',
  styleUrls: ['./alojamiento-card.component.scss'],
})
export class AlojamientoCardComponent {
  @Input() alojamiento!: AlojamientoResponseDTO;
  @Input() imageUrl?: string | null;
}
