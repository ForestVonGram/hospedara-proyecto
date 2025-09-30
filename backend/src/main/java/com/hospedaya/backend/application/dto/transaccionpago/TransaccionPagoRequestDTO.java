package com.hospedaya.backend.application.dto.transaccionpago;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransaccionPagoRequestDTO {
    private Long pagoId;
    private String referenciaExterna;
    private BigDecimal monto;
    private String detalle;
}
