package com.hospedaya.backend.application.dto.pago;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequestDTO {

    @NotNull(message = "El ID de la reserva es obligatorio")
    private Long reservaId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Size(max = 100, message = "La referencia externa no debe exceder 100 caracteres")
    private String referenciaExterna;
}
