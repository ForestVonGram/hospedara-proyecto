package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.application.service.base.TransaccionPagoService;
import com.hospedaya.backend.domain.entity.TransaccionPago;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.TransaccionPagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TransaccionPagoServiceImpl implements TransaccionPagoService {
    
    private final TransaccionPagoRepository transaccionPagoRepository;
    
    public TransaccionPagoServiceImpl(TransaccionPagoRepository transaccionPagoRepository) {
        this.transaccionPagoRepository = transaccionPagoRepository;
    }

    public TransaccionPago registrarTransaccion(TransaccionPago transaccionPago) {
        return transaccionPagoRepository.save(transaccionPago);
    }

    public TransaccionPago obtenerTransaccionPorId(Long id) {
        return transaccionPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción de pago no encontrada con id: " + id));
    }

    public List<TransaccionPago> listarTransacciones() {
        return transaccionPagoRepository.findAll();
    }
}
