import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'perfil', loadComponent: () => import('./pages/perfil/perfil.component').then(m => m.PerfilComponent) },
  { path: 'profile-setup', loadComponent: () => import('./pages/profile-setup/profile-setup.component').then(m => m.ProfileSetupComponent) },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: '**', redirectTo: '' }
];
