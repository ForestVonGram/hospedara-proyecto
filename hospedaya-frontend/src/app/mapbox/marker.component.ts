import { Component, Input } from '@angular/core';

/**
 * Declarative marker component to be used inside <app-mapbox-map>.
 * Example:
 * <app-mapbox-map>
 *   <app-marker [lng]="-74.1" [lat]="4.65" popup="Alojamiento" />
 * </app-mapbox-map>
 *
 * Note: The parent component/template must import MarkerComponent when using it.
 */
@Component({
  selector: 'app-marker',
  standalone: true,
  template: ''
})
export class MarkerComponent {
  @Input() lng!: number;
  @Input() lat!: number;
  @Input() popup?: string;
  // Opciones comunes del marcador de Mapbox
  @Input() color?: string;
  @Input() scale?: number;
  // Para marcadores personalizados con HTML
  @Input() html?: string;
}
