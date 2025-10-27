// Central API configuration
// You can override via window.__API_BASE_URL__ at runtime if needed

export const DEFAULT_API_BASE_URL = 'http://localhost:8080';

// Allow runtime override if hosting sets window.__API_BASE_URL__
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const globalAny: any = globalThis as any;
export const API_BASE_URL: string =
  (globalAny && globalAny.__API_BASE_URL__) || DEFAULT_API_BASE_URL;
