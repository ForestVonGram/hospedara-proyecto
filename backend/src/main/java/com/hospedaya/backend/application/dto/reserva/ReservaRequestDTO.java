package com.hospedaya.backend.application.dto.reserva;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
}
