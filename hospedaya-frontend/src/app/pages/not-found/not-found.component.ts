import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="nf-container">
      <h1>404</h1>
      <p>La página que buscas no existe.</p>
      <a routerLink="/">Ir al inicio</a>
    </div>
  `,
  styles: [`
    .nf-container { min-height: 70vh; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: .75rem; text-align: center; }
    h1 { font-size: 4rem; margin: 0; }
    a { color: #2b6cb0; text-decoration: underline; }
  `]
})
export class NotFoundComponent {}
