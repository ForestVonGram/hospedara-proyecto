import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
}

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  rol: string;
  fechaRegistro: string;
  activo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/usuarios';
  private authUrl = 'http://localhost:8080/auth';
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient) {}

  login(loginData: LoginRequest): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/login`, loginData);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  register(registerData: RegisterRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, registerData);
  }

  // Métodos para gestionar el estado de autenticación
  saveUser(user: any): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  logout(): void {
    localStorage.removeItem('user');
  }

  isLoggedIn(): boolean {
    return this.getUser() !== null;
  }
}
