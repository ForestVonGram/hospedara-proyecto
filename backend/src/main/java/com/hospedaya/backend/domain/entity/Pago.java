package com.hospedaya.backend.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String descripcion;

}
