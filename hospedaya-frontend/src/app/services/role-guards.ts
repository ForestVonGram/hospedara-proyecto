import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();
  if (!token) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
  return true;
};

// Solo anfitrión
export const hostGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();
  if (!token) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
  const user = auth.getUser();
  if (!user || user.rol !== 'ANFITRION') {
    router.navigate(['/dashboard']);
    return false;
  }
  return true;
};

// Solo huésped/usuario autenticado (no anfitrión)
export const userGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();
  if (!token) {
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
  const user = auth.getUser();
  if (!user) {
    router.navigate(['/login']);
    return false;
  }
  if (user.rol === 'ANFITRION') {
    router.navigate(['/dashboard-anfitrion']);
    return false;
  }
  return true;
};

// Público o huésped: permite anonimato; si es anfitrión logueado, redirige a dashboard de anfitrión
export const guestOnlyGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.getToken();
  if (!token) return true; // anónimo permitido
  const user = auth.getUser();
  if (user?.rol === 'ANFITRION') {
    router.navigate(['/dashboard-anfitrion']);
    return false;
  }
  return true;
};
