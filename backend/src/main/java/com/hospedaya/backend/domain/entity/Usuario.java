package com.hospedaya.backend.domain.entity;

import com.hospedaya.backend.domain.enums.Rol;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    private String nombre;

    @Column(unique = true,  nullable = false)
    private String email;

    private String telefono;

    @Column(name = "\"password\"", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private LocalDate fechaRegistro;

    private Boolean activo = true;

    @OneToMany(mappedBy = "usuario")
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "usuario")
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "usuario")
    private List<Favorito> favoritos;

    @OneToMany(mappedBy = "usuario")
    private List<Notificacion> notificaciones;

}
