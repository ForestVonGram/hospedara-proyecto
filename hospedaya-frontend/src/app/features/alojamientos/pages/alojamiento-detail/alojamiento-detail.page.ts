import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { combineLatest, map, Observable, switchMap } from 'rxjs';
import { AlojamientoService } from '../../services/alojamiento.service';
import { AlojamientoResponseDTO } from '../../../../core/services/api/alojamiento-api.service';

@Component({
  selector: 'app-alojamiento-detail-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './alojamiento-detail.page.html',
  styleUrls: ['./alojamiento-detail.page.scss'],
})
export class AlojamientoDetailPage {
  alojamiento$!: Observable<AlojamientoResponseDTO>;
  coverUrl$!: Observable<string | null>;

  vm$!: Observable<{ alojamiento: AlojamientoResponseDTO; coverUrl: string | null }>;

  constructor(private route: ActivatedRoute, private alojamientoService: AlojamientoService) {
    const id$ = this.route.paramMap.pipe(map((p) => Number(p.get('id'))));
    this.alojamiento$ = id$.pipe(switchMap((id) => this.alojamientoService.getById(id)));
    this.coverUrl$ = id$.pipe(switchMap((id) => this.alojamientoService.getCoverImageUrl$(id)));

    this.vm$ = combineLatest([this.alojamiento$, this.coverUrl$]).pipe(
      map(([alojamiento, coverUrl]) => ({ alojamiento, coverUrl }))
    );
  }
}
