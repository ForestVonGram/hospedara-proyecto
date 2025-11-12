import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Toast, ToastService } from './toast.service';

@Component({
  selector: 'app-toasts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toasts">
      <div class="toast" *ngFor="let t of toasts" [class.success]="t.type==='success'" [class.error]="t.type==='error'" [class.info]="t.type==='info'" [class.warning]="t.type==='warning'">
        <span>{{ t.message }}</span>
        <button class="close" (click)="dismiss(t.id)">×</button>
      </div>
    </div>
  `,
  styles: [`
    .toasts { position: fixed; top: 16px; right: 16px; z-index: 2000; display: flex; flex-direction: column; gap: 8px; }
    .toast { display: flex; align-items: center; gap: 12px; background: #222; color: #fff; padding: 10px 14px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,.2); }
    .toast.success { background: #16a34a; }
    .toast.error { background: #dc2626; }
    .toast.info { background: #2563eb; }
    .toast.warning { background: #ca8a04; }
    .close { background: transparent; border: none; color: #fff; font-size: 18px; cursor: pointer; }
  `]
})
export class ToastsComponent implements OnDestroy {
  toasts: Toast[] = [];
  private unsub: (() => void) | null = null;

  constructor(private toastService: ToastService) {
    this.unsub = this.toastService.subscribe(ts => this.toasts = ts);
  }

  dismiss(id: number) { this.toastService.dismiss(id); }

  ngOnDestroy(): void { this.unsub?.(); }
}
