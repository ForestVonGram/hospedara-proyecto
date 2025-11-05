import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
  protected readonly year = new Date().getFullYear();

  ngAfterViewInit() {
    const cdn = 'https://cdn.jsdelivr.net/npm/flatpickr';
    if (!(window as any).flatpickr) {
      const s = document.createElement('script');
      s.src = cdn;
      s.async = true;
      s.onload = () => this.initDatepickers();
      document.head.appendChild(s);
    } else {
      this.initDatepickers();
    }
  }

  private initDatepickers() {
    try {
      (window as any).flatpickr?.('#checkin', { dateFormat: 'Y-m-d' });
      (window as any).flatpickr?.('#checkout', { dateFormat: 'Y-m-d' });
    } catch {}
  }
}
