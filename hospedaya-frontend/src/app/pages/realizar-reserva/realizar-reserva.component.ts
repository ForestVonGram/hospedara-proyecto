import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AlojamientoResponseDTO, AlojamientoService } from '../../services/alojamiento.service';
import { AuthService } from '../../services/auth.service';
import { ReservaRequest, ReservaService } from '../../services/reserva.service';
import { PagoService } from '../../services/pago.service';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-realizar-reserva',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, HeaderComponent],
  templateUrl: './realizar-reserva.component.html',
  styleUrls: ['./realizar-reserva.component.css']
})
export class RealizarReservaComponent implements OnInit {
  private route = inject(ActivatedRoute);

  alojamientoId?: number;
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private alojamientoService = inject(AlojamientoService);
  private auth = inject(AuthService);
  private reservaService = inject(ReservaService);
  private pagoService = inject(PagoService);

  form!: FormGroup;
  alojamiento?: AlojamientoResponseDTO;
  cargando = false;
  error = '';
  exito = '';

  maxHuespedes(): number {
    const m = (this.alojamiento as any)?.maxHuespedes;
    const val = typeof m === 'number' ? m : Number(m);
    return Number.isFinite(val) && val > 0 ? val : 10; // default razonable si no viene definido
  }

  huespedesOpciones(): number[] {
    const max = this.maxHuespedes();
    return Array.from({ length: max }, (_, i) => i + 1);
  }

  noches(): number {
    const i = this.form?.get('fechaInicio')?.value as string;
    const f = this.form?.get('fechaFin')?.value as string;
    if (!i || !f) return 0;
    // Normalizamos a medianoche local para evitar problemas de zona horaria
    const inicio = new Date(i + 'T00:00:00');
    const fin = new Date(f + 'T00:00:00');
    const diff = fin.getTime() - inicio.getTime();
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return isNaN(days) || days <= 0 ? 0 : days;
  }

  total(): number {
    const noches = this.noches();
    const precio = this.alojamiento?.precioPorNoche ?? 0;
    return noches * precio;
  }

  todayStr(): string {
    const d = new Date();
    d.setHours(0,0,0,0);
    const m = (d.getMonth()+1).toString().padStart(2,'0');
    const day = d.getDate().toString().padStart(2,'0');
    return `${d.getFullYear()}-${m}-${day}`;
  }

  private getFechaInicioDate(): Date | null {
    const i = this.form?.get('fechaInicio')?.value as string;
    if (!i) return null;
    const d = new Date(i + 'T00:00:00');
    d.setHours(0,0,0,0);
    return isNaN(d.getTime()) ? null : d;
  }

  minCheckoutStr(): string {
    const inicio = this.getFechaInicioDate();
    const base = inicio && inicio.getTime() > 0 ? new Date(inicio) : new Date();
    base.setHours(0,0,0,0);
    // al menos un día después del inicio
    base.setDate(base.getDate() + 1);
    const m = (base.getMonth()+1).toString().padStart(2,'0');
    const day = base.getDate().toString().padStart(2,'0');
    return `${base.getFullYear()}-${m}-${day}`;
  }

  ajustarMinCheckout() {
    const finCtrl = this.form?.get('fechaFin');
    const finVal = finCtrl?.value as string;
    const minFin = this.minCheckoutStr();
    if (finVal && finVal < minFin) {
      finCtrl?.setValue(minFin);
    }
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      fechaInicio: [null, Validators.required],
      fechaFin: [null, Validators.required],
      huespedes: [1, [Validators.required, Validators.min(1)]]
    });

    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    this.alojamientoId = Number.isFinite(id) ? id : undefined;
    if (!id || isNaN(id)) {
      this.error = 'Alojamiento inválido';
      return;
    }

    // Prefill desde query params si vienen del detalle
    const qi = this.route.snapshot.queryParamMap.get('fechaInicio');
    const qf = this.route.snapshot.queryParamMap.get('fechaFin');
    if (qi) this.form.get('fechaInicio')?.setValue(qi);
    if (qf) this.form.get('fechaFin')?.setValue(qf);

    this.cargando = true;
    this.alojamientoService.obtenerPorId(id).subscribe({
      next: (a) => {
        this.alojamiento = a;
        // ajustar validador de huéspedes según capacidad
        const max = this.maxHuespedes();
        const ctrl = this.form.get('huespedes');
        if (ctrl) {
          ctrl.setValidators([Validators.required, Validators.min(1), Validators.max(max)]);
          ctrl.updateValueAndValidity();
          // si el valor actual excede, ajustarlo
          const v = Number(ctrl.value || 1);
          if (v > max) ctrl.setValue(max);
        }
      },
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

    // Validación extra: no permitir fechas pasadas y coherencia
    const hoy = new Date(this.todayStr() + 'T00:00:00');
    const inicioDate = new Date(fechaInicio + 'T00:00:00');
    const finDate = new Date(fechaFin + 'T00:00:00');
    if (inicioDate < hoy) {
      this.error = 'La fecha de inicio no puede ser anterior a hoy.';
      return;
    }
    if (!(finDate > inicioDate)) {
      this.error = 'La fecha de fin debe ser posterior a la fecha de inicio.';
      return;
    }

    // Validar huéspedes vs capacidad
    const huespedes: number = Number(this.form.get('huespedes')?.value || 1);
    if (huespedes > this.maxHuespedes()) {
      this.error = 'La cantidad de huéspedes supera la capacidad máxima permitida.';
      return;
    }

    const payload: ReservaRequest = {
      usuarioId: Number(user.id),
      alojamientoId: Number(this.alojamiento.id),
      fechaInicio,
      fechaFin
    };

    this.cargando = true;
    this.reservaService.crearReserva(payload).subscribe({
      next: (reserva) => {
        // Con la reserva creada, registramos un pago por el total calculado
        const monto = this.total();
        if (monto <= 0) {
          this.error = 'El monto a pagar es inválido.';
          this.cargando = false;
          return;
        }
        this.pagoService.registrarPago({
          reservaId: Number(reserva.id),
          monto: Number(monto),
          referenciaExterna: `RES-${reserva.id}`
        }).subscribe({
          next: (pago) => {
            // Iniciamos el checkout de Mercado Pago
            this.pagoService.iniciarPago(Number(pago.id)).subscribe({
              next: (resp) => {
                const url = resp?.init_point;
                if (url) {
                  window.location.href = url;
                } else {
                  this.error = 'No se pudo obtener la URL de pago.';
                }
              },
              error: (err2) => {
                this.error = typeof err2?.error === 'string' && err2.error.trim().length > 0
                  ? err2.error
                  : 'Error al iniciar el pago.';
              },
              complete: () => {
                this.cargando = false;
              }
            });
          },
          error: (err1) => {
            this.error = typeof err1?.error === 'string' && err1.error.trim().length > 0
              ? err1.error
              : 'No se pudo registrar el pago.';
            this.cargando = false;
          }
        });
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
        this.cargando = false;
      }
    });
  }
}
