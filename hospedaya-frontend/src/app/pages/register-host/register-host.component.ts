import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface RegisterHostRequest {
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
  rol: 'ANFITRION';
}

@Component({
  selector: 'app-register-host',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register-host.component.html',
  styleUrl: './register-host.component.css'
})
export class RegisterHostComponent {
  registerData: RegisterHostRequest = {
    nombre: '',
    email: '',
    password: '',
    telefono: '',
    rol: 'ANFITRION'
  };

  confirmPassword: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    // Validaciones
    if (!this.registerData.nombre || !this.registerData.email || !this.registerData.password) {
      this.errorMessage = 'Por favor, completa todos los campos obligatorios';
      return;
    }

    if (this.registerData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (this.registerData.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Llamar al mismo endpoint de registro, incluyendo rol=ANFITRION
    this.authService.register(this.registerData as any).subscribe({
      next: (response) => {
        console.log('Registro de anfitrión exitoso', response);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error en registro de anfitrión', error);

        if (error.status === 400) {
          this.errorMessage = 'Datos inválidos. Verifica la información ingresada.';
        } else if (error.status === 409) {
          this.errorMessage = 'El email ya está registrado';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (typeof error.error === 'string' && error.error.trim().length > 0) {
          this.errorMessage = error.error;
        } else {
          this.errorMessage = 'Error al registrar anfitrión. Intenta nuevamente.';
        }
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
