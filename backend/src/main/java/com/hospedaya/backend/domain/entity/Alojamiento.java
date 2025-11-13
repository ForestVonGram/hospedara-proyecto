package com.hospedaya.backend.domain.entity;

import com.hospedaya.backend.domain.enums.EstadoAlojamiento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alojamientos")
@Getter
@Setter
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Double precioPorNoche;

    @Column(name = "max_huespedes")
    private Integer maxHuespedes; // capacidad máxima de huéspedes

    @Enumerated(EnumType.STRING)
    private EstadoAlojamiento estado = EstadoAlojamiento.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private Usuario anfitrion; // El usuario con rol ANFITRION

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenAlojamiento> imagenes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "alojamiento_servicios",
            joinColumns = @JoinColumn(name = "alojamiento_id"),
            inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> servicios = new ArrayList<>();

    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
