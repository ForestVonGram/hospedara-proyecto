import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginData: LoginRequest = {
    email: '',
    password: ''
  };

  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.loginData.email || !this.loginData.password) {
      this.errorMessage = 'Por favor, completa todos los campos';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Login exitoso', response);
        // Guardar el token JWT
        if (response.token) {
          this.authService.setToken(response.token);
          // Cargar y cachear el perfil completo para tener foto/telefono en toda la app
          this.usuarioService.me().subscribe({
            next: (u) => {
              this.authService.saveUser(u);
              // Redirigir según el rol del usuario
              if (u.rol === 'ANFITRION') {
                this.router.navigate(['/dashboard-anfitrion']);
              } else {
                this.router.navigate(['/dashboard']);
              }
            },
            error: (err) => {
              console.error('Error al cargar perfil', err);
              // Si falla cargar el perfil, redirigir a landing
              this.router.navigate(['/']);
            }
          });
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error en login', error);
        
        if (error.status === 404) {
          this.errorMessage = 'Email no encontrado';
        } else if (error.status === 401) {
          this.errorMessage = 'Contraseña incorrecta';
        } else {
          this.errorMessage = 'Error al iniciar sesión. Intenta nuevamente.';
        }
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
