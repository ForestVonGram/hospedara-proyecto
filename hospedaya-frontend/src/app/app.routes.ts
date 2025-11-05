import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'register-host', loadComponent: () => import('./pages/register-host/register-host.component').then(m => m.RegisterHostComponent) },
  { path: 'recuperar-password', loadComponent: () => import('./pages/recuperar-password/recuperar-password.component').then(m => m.RecuperarPasswordComponent) },
  { path: 'reset-password', loadComponent: () => import('./pages/reset-password/reset-password.component').then(m => m.ResetPasswordComponent) },
  { path: 'profile-setup', loadComponent: () => import('./pages/profile-setup/profile-setup.component').then(m => m.ProfileSetupComponent) },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'alojamientos/gestion', loadComponent: () => import('./pages/gestion-alojamientos/gestion-alojamientos.component').then(m => m.GestionAlojamientosComponent) },
  { path: 'alojamientos/nuevo', loadComponent: () => import('./pages/alojamiento-creation/alojamiento-creation.component').then(m => m.AlojamientoCreationComponent) },
  { path: '**', redirectTo: '' }
];
