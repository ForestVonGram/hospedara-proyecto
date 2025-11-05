import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'recuperar-password', loadComponent: () => import('./pages/recuperar-password/recuperar-password.component').then(m => m.RecuperarPasswordComponent) },
  { path: 'reset-password', loadComponent: () => import('./pages/reset-password/reset-password.component').then(m => m.ResetPasswordComponent) },
  { path: 'perfil', loadComponent: () => import('./pages/profile-setup/profile-setup.component').then(m => m.ProfileSetupComponent) },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'reservas', loadComponent: () => import('./pages/reservas/reservas.component').then(m => m.ReservasComponent) },
  { path: 'buscar', loadComponent: () => import('./pages/resultados/resultados.component').then(m => m.ResultadosComponent) },
  { path: '**', redirectTo: '' }
];
