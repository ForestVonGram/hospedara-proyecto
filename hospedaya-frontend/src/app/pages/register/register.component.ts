import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerData: RegisterRequest = {
    nombre: '',
    email: '',
    password: '',
    telefono: ''
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

    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        console.log('Registro exitoso', response);
        // Redirigir al login después del registro exitoso
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error en registro', error);
        
        if (error.status === 400) {
          this.errorMessage = 'Datos inválidos. Verifica la información ingresada.';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Error al registrar usuario. Intenta nuevamente.';
        }
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
