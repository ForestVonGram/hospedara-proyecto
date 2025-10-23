import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="container">
      <h2>Iniciar sesión</h2>
      <form (ngSubmit)="onSubmit()" #f="ngForm" class="form">
        <label>
          Correo
          <input name="email" type="email" required ngModel />
        </label>
        <label>
          Contraseña
          <input name="password" type="password" required ngModel />
        </label>
        <button type="submit">Entrar</button>
      </form>
    </section>
  `,
  styles: [`
    .container { padding: 1.5rem; max-width: 480px; }
    .form { display: grid; gap: .75rem; margin-top: .5rem; }
    input { width: 100%; padding: .5rem; }
    button { padding: .5rem .75rem; }
  `]
})
export class LoginComponent {
  onSubmit() {
    // TODO: implementar autenticación real
    alert('Inicio de sesión simulado');
  }
}
