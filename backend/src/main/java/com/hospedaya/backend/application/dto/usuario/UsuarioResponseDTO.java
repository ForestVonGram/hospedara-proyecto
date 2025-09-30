package com.hospedaya.backend.application.dto.usuario;

import com.hospedaya.backend.domain.enums.Rol;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private Rol rol;
    private LocalDate fechaRegistro;
    private Boolean activo;
}
