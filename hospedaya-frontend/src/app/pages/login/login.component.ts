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
  styleUrls: ['./login.component.css']
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

    // Asegurar que no quede sesión previa (evita usar token viejo en /usuarios/me)
    this.authService.logout();

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        console.log('Login exitoso', response);
        // Guardar el token JWT
        if (response.token) {
          this.authService.setToken(response.token);
          // Cargar y cachear el perfil completo para tener foto/telefono en toda la app
          this.usuarioService.me().subscribe({
            next: (u) => {
              // Validar que el perfil corresponda al email que inició sesión
              const loggedEmail = this.loginData.email.trim().toLowerCase();
              const profileEmail = (u.email || '').trim().toLowerCase();
              if (loggedEmail && profileEmail && loggedEmail !== profileEmail) {
                console.error('Email de perfil no coincide con el login. Previniendo mezcla de sesión.', { loggedEmail, profileEmail });
                this.authService.logout();
                this.errorMessage = 'Hubo un problema con la sesión. Intenta iniciar sesión nuevamente.';
                this.router.navigate(['/login']);
                return;
              }

              this.authService.saveUser(u);
              // Redirigir según el rol del usuario
              if (u.rol === 'ANFITRION') {
                this.router.navigate(['/dashboard-anfitrion']);
              } else if (u.rol === 'ADMIN') {
                this.router.navigate(['/admin']);
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
        } else if (error.status === 423) {
          this.errorMessage = 'Tu cuenta ha sido bloqueada por múltiples intentos fallidos. Podrás volver a entrar tras 15 minutos de espera.';
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
