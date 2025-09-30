package com.hospedaya.backend.application.dto.pago;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequestDTO {

    private Long reservaId;
    private BigDecimal monto;
    private String referenciaExterna;
}
