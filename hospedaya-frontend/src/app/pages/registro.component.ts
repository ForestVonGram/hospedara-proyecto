import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="container">
      <h2>Crear cuenta</h2>
      <form class="form" (ngSubmit)="onSubmit()" #f="ngForm">
        <label>
          Nombre
          <input name="nombre" type="text" required ngModel />
        </label>
        <label>
          Correo
          <input name="email" type="email" required ngModel />
        </label>
        <label>
          Contraseña
          <input name="password" type="password" required ngModel />
        </label>
        <button type="submit">Registrarme</button>
      </form>
    </section>
  `,
  styles: [`
    .container { padding: 1.5rem; max-width: 520px; }
    .form { display: grid; gap: .75rem; margin-top: .5rem; }
    input { width: 100%; padding: .5rem; }
    button { padding: .5rem .75rem; }
  `]
})
export class RegistroComponent {
  onSubmit() {
    alert('Registro simulado');
  }
}
