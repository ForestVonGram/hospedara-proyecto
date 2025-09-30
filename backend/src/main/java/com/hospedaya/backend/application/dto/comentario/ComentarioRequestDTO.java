package com.hospedaya.backend.application.dto.comentario;

import lombok.Data;

@Data
public class ComentarioRequestDTO {

    private Long usuarioId;
    private Long alojamientoId;
    private String texto;
    private int calificacion;
}
