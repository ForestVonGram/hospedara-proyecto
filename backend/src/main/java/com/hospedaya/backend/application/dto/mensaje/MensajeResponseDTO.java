package com.hospedaya.backend.application.dto.mensaje;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MensajeResponseDTO {
    private Long id;
    private Long emisorId;
    private String emisorNombre;
    private Long receptorId;
    private String receptorNombre;
    private Long alojamientoId;
    private String alojamientoNombre;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
}