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
    private String nombre;
    private String descripcion;
    private String direccion;
    private BigDecimal precioPorNoche;
    private Integer maxHuespedes;
}
