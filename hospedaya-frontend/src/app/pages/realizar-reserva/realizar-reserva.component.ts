import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AlojamientoResponseDTO, AlojamientoService } from '../../services/alojamiento.service';
import { AuthService } from '../../services/auth.service';
import { ReservaRequest, ReservaService } from '../../services/reserva.service';

@Component({
  selector: 'app-realizar-reserva',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './realizar-reserva.component.html',
  styleUrl: './realizar-reserva.component.css'
})
export class RealizarReservaComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private alojamientoService = inject(AlojamientoService);
  private auth = inject(AuthService);
  private reservaService = inject(ReservaService);

  form!: FormGroup;
  alojamiento?: AlojamientoResponseDTO;
  cargando = false;
  error = '';
  exito = '';

  noches = computed(() => {
    const i = this.form?.get('fechaInicio')?.value as string;
    const f = this.form?.get('fechaFin')?.value as string;
    if (!i || !f) return 0;
    const inicio = new Date(i);
    const fin = new Date(f);
    const diff = fin.getTime() - inicio.getTime();
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return isNaN(days) || days <= 0 ? 0 : days;
  });

  total = computed(() => {
    const noches = this.noches();
    const precio = this.alojamiento?.precioPorNoche ?? 0;
    return noches * precio;
  });

  ngOnInit(): void {
    this.form = this.fb.group({
      fechaInicio: [null, Validators.required],
      fechaFin: [null, Validators.required],
      huespedes: [1, [Validators.required, Validators.min(1)]]
    });

    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (!id || isNaN(id)) {
      this.error = 'Alojamiento inválido';
      return;
    }

    this.cargando = true;
    this.alojamientoService.obtenerPorId(id).subscribe({
      next: (a) => (this.alojamiento = a),
      error: () => (this.error = 'No se pudo cargar el alojamiento'),
      complete: () => (this.cargando = false)
    });
  }

  confirmarReserva(): void {
    this.error = '';
    this.exito = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const user = this.auth.getUser();
    if (!user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.alojamiento?.id) {
      this.error = 'Alojamiento no disponible';
      return;
    }

    const fechaInicio: string = this.form.get('fechaInicio')?.value;
    const fechaFin: string = this.form.get('fechaFin')?.value;

    const payload: ReservaRequest = {
      usuarioId: Number(user.id),
      alojamientoId: Number(this.alojamiento.id),
      fechaInicio,
      fechaFin
    };

    this.cargando = true;
    this.reservaService.crearReserva(payload).subscribe({
      next: () => {
        this.exito = 'Reserva creada correctamente';
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        if (typeof err?.error === 'string' && err.error.trim().length > 0) {
          this.error = err.error;
        } else if (err?.error?.message) {
          this.error = err.error.message;
        } else if (err.status === 400) {
          this.error = 'Datos inválidos de reserva. Verifica las fechas.';
        } else if (err.status === 404) {
          this.error = 'Usuario o alojamiento no encontrado.';
        } else {
          this.error = 'No se pudo crear la reserva. Intenta nuevamente.';
        }
      },
      complete: () => (this.cargando = false)
    });
  }
}
