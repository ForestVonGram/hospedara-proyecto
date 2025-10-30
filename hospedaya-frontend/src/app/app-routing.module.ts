import { Routes } from '@angular/router';
import { HomePage } from './features/home/home-page/home-page';
import { LoginPage } from './features/auth/pages/login/login-page';
import { RegistroPage } from './features/auth/pages/registro/registro-page';
import { NotFoundComponent } from './pages/not-found.component';
import { AlojamientosListPage } from './features/alojamientos/pages/alojamientos-list/alojamientos-list.page';
import { AlojamientoDetailPage } from './features/alojamientos/pages/alojamiento-detail/alojamiento-detail.page';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'login', component: LoginPage },
  { path: 'registro', component: RegistroPage },
  { path: 'alojamientos', component: AlojamientosListPage },
  { path: 'alojamientos/:id', component: AlojamientoDetailPage },
  { path: '**', component: NotFoundComponent }
];
