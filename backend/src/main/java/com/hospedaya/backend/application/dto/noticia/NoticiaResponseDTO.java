package com.hospedaya.backend.application.dto.noticia;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticiaResponseDTO {

    private Long id;
    private String titulo;
    private String resumen;
    private String contenido;
    private LocalDateTime fechaCreacion;
}
