import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

interface PagoErrorInfo {
  transaccionId?: string | number;
  monto?: number;
  estado?: string;
}

@Component({
  selector: 'app-pago-incorrecto',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pago-incorrecto.component.html',
  styleUrls: ['./pago-incorrecto.component.css']
})
export class PagoIncorrectoComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  pagoInfo?: PagoErrorInfo;
  errorDetalle = '';

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      // Si no hay token de éxito, se asume pago incorrecto; leemos detalles si existen
      const status = params.get('status') ?? params.get('collection_status');
      const reason = params.get('reason') ?? params.get('error_message');
      const txId = params.get('transaction_id') ?? params.get('payment_id') ?? undefined;
      const montoStr = params.get('monto') ?? undefined;

      const monto = montoStr ? Number(montoStr) : undefined;
      this.pagoInfo = {
        transaccionId: txId,
        monto: monto && !isNaN(monto) ? monto : undefined,
        estado: status ?? 'Rechazado'
      };
      this.errorDetalle = reason ?? '';
    });
  }

  intentarDeNuevo() {
    // Si tienes una ruta específica para reintentar el pago, navega allí.
    // Por defecto, llevamos al usuario al inicio.
    this.router.navigate(['/']);
  }

  verMisReservas() {
    this.router.navigate(['/dashboard']);
  }
}
