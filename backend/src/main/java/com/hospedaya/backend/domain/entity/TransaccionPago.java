package com.hospedaya.backend.domain.entity;

import com.hospedaya.backend.domain.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaccionesPago")
public class TransaccionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pago_id")
    private Pago pago;

    @Column(nullable = false)
    private String referenciaExterna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(length = 500)
    private String detalle;
}
