package com.hospedaya.backend.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para transferir información de un usuario")
public class UsuarioDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del usuario", example = "David Gómez")
    private String nombre;

    @Schema(description = "Correo electrónico del usuario", example = "david@example.com")
    private String email;

    @Schema(description = "Rol del usuario (CLIENTE o ANFITRION)", example = "CLIENTE")
    private String rol;
}
