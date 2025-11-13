import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';

import { routes } from './app.routes';
import { AuthInterceptor } from './services/auth.interceptor';
import { MAPBOX_TOKEN } from './mapbox/mapbox.config';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    // Habilita interceptores provistos via DI (HTTP_INTERCEPTORS)
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    // Mapbox access token provider (reads from <meta name="mapbox-token" content="...">)
    { provide: MAPBOX_TOKEN, useFactory: () => {
      const el = document.querySelector('meta[name="mapbox-token"]') as HTMLMetaElement | null;
      return el?.content || '';
    }}
  ]
};
