package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.PagoService;
import com.hospedaya.backend.domain.entity.Pago;
import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.enums.EstadoPago;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.PagoRepository;
import com.hospedaya.backend.infraestructure.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public PagoServiceImpl(PagoRepository pagoRepository, ReservaRepository reservaRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public Pago registrarPago(Pago pago) {
        // Validar y enlazar la reserva desde BD
        Long reservaId = pago.getReserva() != null ? pago.getReserva().getId() : null;
        if (reservaId == null) {
            throw new ResourceNotFoundException("Se requiere el ID de la reserva para registrar el pago");
        }
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + reservaId));
        pago.setReserva(reserva);

        // Asegurar referencia externa
        if (pago.getReferenciaExterna() == null || pago.getReferenciaExterna().isBlank()) {
            pago.setReferenciaExterna("PAY-" + UUID.randomUUID());
        }

        // Establecer estado del pago y fecha de confirmación
        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaConfirmacion(LocalDateTime.now());

        // Guardar pago
        Pago creado = pagoRepository.save(pago);

        // Actualizar estado de la reserva a PAGADA
        reserva.setEstado(EstadoReserva.PAGADA);
        reservaRepository.save(reserva);

        return creado;
    }

    @Override
    public Pago obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
    }

    @Override
    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    @Override
    public void eliminarPago(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado con ID: " + id);
        }
        pagoRepository.deleteById(id);
    }
}
