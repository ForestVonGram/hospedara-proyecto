import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="container">
      <h2>Página no encontrada</h2>
      <p>La ruta que intentas visitar no existe.</p>
      <a routerLink="/">Volver al inicio</a>
    </section>
  `,
  styles: [`
    .container { padding: 1.5rem; }
    a { display: inline-block; margin-top: .75rem; }
  `]
})
export class NotFoundComponent {}
