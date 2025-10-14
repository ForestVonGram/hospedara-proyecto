package com.hospedaya.backend.application.dto.reserva;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ReservaRequestDTO {

    @NotNull
    @JsonAlias({"idUsuario", "usuarioID", "UsuarioId"})
    private Long usuarioId;

    @NotNull
    @JsonAlias({"idAlojamiento", "alojamiendoId", "alojamientoID", "AlojamientoId"})
    private Long alojamientoId;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;
}
