import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { authGuard, hostGuard, userGuard, guestOnlyGuard } from './services/role-guards';

export const routes: Routes = [
  // Rutas públicas
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'register-host', loadComponent: () => import('./pages/register-host/register-host.component').then(m => m.RegisterHostComponent) },
  { path: 'recuperar-password', loadComponent: () => import('./pages/recuperar-password/recuperar-password.component').then(m => m.RecuperarPasswordComponent) },
  { path: 'reset-password', loadComponent: () => import('./pages/reset-password/reset-password.component').then(m => m.ResetPasswordComponent) },

  // Rutas protegidas - Huéspedes (HUESPED)
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [userGuard]
  },
  {
    path: 'resultados',
    loadComponent: () => import('./pages/resultados/resultados.component').then(m => m.ResultadosComponent),
    canActivate: [guestOnlyGuard] // Permite búsqueda sin login
  },
  {
    path: 'alojamientos/:id/reservar',
    loadComponent: () => import('./pages/realizar-reserva/realizar-reserva.component').then(m => m.RealizarReservaComponent),
    canActivate: [authGuard] // Requiere estar logueado
  },
  {
    path: 'reservas',
    loadComponent: () => import('./pages/reservas/reservas.component').then(m => m.ReservasComponent),
    canActivate: [userGuard] // Solo huéspedes
  },

  // Rutas protegidas - Anfitriones (ANFITRION)
  {
    path: 'dashboard-anfitrion',
    loadComponent: () => import('./pages/dashboard-anfitrion/dashboard-anfitrion.component').then(m => m.DashboardAnfitrionComponent),
    canActivate: [hostGuard]
  },
  {
    path: 'alojamientos/gestion',
    loadComponent: () => import('./pages/gestion-alojamientos/gestion-alojamientos.component').then(m => m.GestionAlojamientosComponent),
    canActivate: [hostGuard]
  },
  {
    path: 'alojamientos/nuevo',
    loadComponent: () => import('./pages/alojamiento-creation/alojamiento-creation.component').then(m => m.AlojamientoCreationComponent),
    canActivate: [hostGuard]
  },

  // Profile setup (disponible para todos los usuarios autenticados)
  {
    path: 'profile-setup',
    loadComponent: () => import('./pages/profile-setup/profile-setup.component').then(m => m.ProfileSetupComponent),
    canActivate: [authGuard]  // Solo requiere estar autenticado
  },

  { path: '**', redirectTo: '' }
];
