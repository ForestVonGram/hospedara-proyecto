package com.hospedaya.backend.application.dto.transaccionpago;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransaccionPagoResponseDTO {

    private Long id;
    private Long pagoId;
    private String referenciaExterna;
    private BigDecimal monto;
    private String estado;
    private LocalDateTime fecha;
    private String detalle;
}
