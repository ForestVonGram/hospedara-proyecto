package com.hospedaya.backend.application.dto.alojamiento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlojamientoUpdateDTO {
    private String titulo;
    private String descripcion;
    private BigDecimal precioPorNoche;
}
