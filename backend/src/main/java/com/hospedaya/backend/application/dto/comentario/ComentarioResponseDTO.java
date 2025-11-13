package com.hospedaya.backend.application.dto.comentario;

import lombok.Data;

@Data
public class ComentarioResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long alojamientoId;
    private String alojamientoNombre;
    private String texto;
    private int calificacion;
}
