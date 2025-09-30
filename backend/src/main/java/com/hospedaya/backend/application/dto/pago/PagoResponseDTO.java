package com.hospedaya.backend.application.dto.pago;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {

    private Long id;
    private Long reservaId;
    private BigDecimal monto;
    private String estado;
    private String referenciaExterna;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaConfirmacion;
}
