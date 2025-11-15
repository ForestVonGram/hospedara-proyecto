package com.hospedaya.backend.application.dto.noticia;

import lombok.Data;

@Data
public class NoticiaRequestDTO {

    private String titulo;
    private String resumen;
    private String contenido;
}
