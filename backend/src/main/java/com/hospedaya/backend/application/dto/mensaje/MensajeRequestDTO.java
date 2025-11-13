package com.hospedaya.backend.application.dto.mensaje;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensajeRequestDTO {
    private Long receptorId;
    private Long alojamientoId;
    private String contenido;
}