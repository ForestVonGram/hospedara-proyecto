import { Component, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import flatpickr from 'flatpickr';
import { Spanish } from 'flatpickr/dist/l10n/es.js';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements AfterViewInit {

  constructor(private router: Router) {}

  // Método para formatear fechas
  private formatDate(date: Date): string {
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  }

  // Métodos de navegación
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/registro']);
  }

  navigateToHome(): void {
    this.router.navigate(['/']);
  }

  ngAfterViewInit(): void {
    // Inicializa Flatpickr para el rango de fechas
    const dateRangePicker = document.getElementById('date-range') as HTMLInputElement;
    if (dateRangePicker) {
      const fp = flatpickr(dateRangePicker, {
        altInput: true,
        altFormat: 'F j, Y',
        dateFormat: 'Y-m-d',
        minDate: 'today',
        locale: Spanish,
        mode: 'range',
        showMonths: 2,
        static: true,
        position: 'below',
        monthSelectorType: 'static',
        prevArrow: '<svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M10 4L6 8L10 12" stroke="#5A3BEF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
        nextArrow: '<svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M6 4L10 8L6 12" stroke="#5A3BEF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
        onChange: (selectedDates) => {
          if (selectedDates.length === 2) {
            // Actualizar los campos de check-in y check-out con las fechas seleccionadas
            const checkinInput = document.getElementById('checkin') as HTMLInputElement;
            const checkoutInput = document.getElementById('checkout') as HTMLInputElement;

            if (checkinInput && checkoutInput) {
              checkinInput.value = this.formatDate(selectedDates[0]);
              checkoutInput.value = this.formatDate(selectedDates[1]);
            }
          }
        }
      });
    }

    // Menú responsive
    const header = document.querySelector('.site-header') as HTMLElement;
    const nav = document.querySelector('.main-nav') as HTMLElement;
    const menuToggle = document.createElement('button');

    menuToggle.innerHTML = `
      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
      viewBox="0 0 24 24" fill="none" stroke="currentColor"
      stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <line x1="3" y1="12" x2="21" y2="12"></line>
      <line x1="3" y1="6" x2="21" y2="6"></line>
      <line x1="3" y1="18" x2="21" y2="18"></line></svg>
    `;
    menuToggle.classList.add('menu-toggle');
    menuToggle.setAttribute('aria-expanded', 'false');
    menuToggle.setAttribute('aria-label', 'Toggle menu');

    menuToggle.addEventListener('click', () => {
      const expanded = menuToggle.getAttribute('aria-expanded') === 'true';
      menuToggle.setAttribute('aria-expanded', (!expanded).toString());
      nav.style.display = expanded ? 'none' : 'flex';
    });

    if (window.innerWidth <= 900) {
      header.appendChild(menuToggle);
      nav.style.display = 'none';
    }

    window.addEventListener('resize', () => {
      if (window.innerWidth > 900) {
        nav.style.display = 'flex';
        menuToggle.style.display = 'none';
      } else {
        menuToggle.style.display = 'block';
      }
    });

    // Maneja el formulario
    const form = document.querySelector('.search-card') as HTMLFormElement;
    form?.addEventListener('submit', (event) => {
      event.preventDefault();
      const dest = (document.getElementById('dest') as HTMLInputElement).value;
      const checkin = (document.getElementById('checkin') as HTMLInputElement).value;
      const checkout = (document.getElementById('checkout') as HTMLInputElement).value;
      const guests = (document.getElementById('guests') as HTMLInputElement).value;

      console.log('🧭 Búsqueda:', { dest, checkin, checkout, guests });
      // Navegar a la página de inicio con los parámetros de búsqueda
      this.router.navigate(['/'], {
        queryParams: {
          destino: dest,
          checkin: checkin,
          checkout: checkout,
          huespedes: guests
        }
      });
    });
  }
}
