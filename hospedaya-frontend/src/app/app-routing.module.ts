import { Routes } from '@angular/router';
import { HomePage } from './features/home/home-page/home-page';
import { LoginPage } from './features/auth/login/login-page/login-page';
import { RegistroPage } from './features/registro/registro-page/registro-page';
import { NotFoundComponent } from './atomic/pages/not-found.component';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'login', component: LoginPage },
  { path: 'registro', component: RegistroPage },
  { path: '**', component: NotFoundComponent }
];
