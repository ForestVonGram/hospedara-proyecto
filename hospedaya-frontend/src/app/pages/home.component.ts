import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  template: `
    <section class="container">
      <h1>HospedaYa</h1>
      <p>Bienvenido a la plataforma de alojamiento.</p>
    </section>
  `,
  styles: [`
    .container { padding: 1.5rem; }
    h1 { margin: 0 0 .5rem; }
  `]
})
export class HomeComponent {}
