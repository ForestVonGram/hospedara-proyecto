package com.hospedaya.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para los pagos realizados por las reservas")
public class PagoDTO {

    @Schema(description = "ID del pago", example = "20")
    private Long id;

    @Schema(description = "ID de la reserva asociada al pago", example = "5")
    private Long reservaId;

    @Schema(description = "Monto pagado", example = "250.75")
    private Double monto;

    @Schema(description = "Método de pago", example = "TARJETA_CREDITO")
    private String metodoPago;

    @Schema(description = "Fecha del pago", example = "2025-09-10")
    private String fechaPago;
}
