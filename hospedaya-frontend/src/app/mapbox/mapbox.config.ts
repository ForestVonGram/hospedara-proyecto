import { InjectionToken } from '@angular/core';

/**
 * Injection token to provide your Mapbox public access token.
 * We will read it from a meta tag in index.html by default.
 */
export const MAPBOX_TOKEN = new InjectionToken<string>('MAPBOX_TOKEN');
