import { Injectable } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  id: number;
  message: string;
  type: ToastType;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toasts: Toast[] = [];
  private nextId = 1;
  listeners: ((toasts: Toast[]) => void)[] = [];

  subscribe(listener: (toasts: Toast[]) => void): () => void {
    this.listeners.push(listener);
    // emit current
    listener(this.toasts);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }

  private emit() {
    for (const l of this.listeners) l(this.toasts);
  }

  show(message: string, type: ToastType = 'info', durationMs = 3000) {
    const toast: Toast = { id: this.nextId++, message, type };
    this.toasts = [...this.toasts, toast];
    this.emit();
    if (durationMs > 0) {
      setTimeout(() => this.dismiss(toast.id), durationMs);
    }
  }

  dismiss(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.emit();
  }

  clear() {
    this.toasts = [];
    this.emit();
  }
}
