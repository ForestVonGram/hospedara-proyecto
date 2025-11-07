import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

interface PagoInfo {
  transaccionId: string | number;
  monto: number;
}

@Component({
  selector: 'app-pago-correcto',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pago-correcto.component.html',
  styleUrls: ['./pago-correcto.component.css']
})
export class PagoCorrectoComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  pagoInfo?: PagoInfo;

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      // Mercado Pago retorna típicamente 'payment_id' y 'status' en la URL.
      // Consideramos éxito si status/collection_status es 'approved' o si hay payment_id.
      const status = (params.get('status') ?? params.get('collection_status') ?? '').toLowerCase();
      const txId = params.get('transaction_id') ?? params.get('payment_id') ?? '';
      const montoStr = params.get('monto');

      const esExitoso = status === 'approved' || (!!txId && txId.length > 0);
      if (esExitoso) {
        const monto = montoStr ? Number(montoStr) : 0;
        this.pagoInfo = {
          transaccionId: txId ?? '',
          monto: isNaN(monto) ? 0 : monto
        };
      } else {
        this.pagoInfo = undefined;
      }
    });
  }

  verMisReservas() {
    this.router.navigate(['/dashboard']);
  }
}
