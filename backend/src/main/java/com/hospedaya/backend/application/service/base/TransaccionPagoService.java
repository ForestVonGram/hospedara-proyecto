package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.TransaccionPago;

import java.util.List;

public interface TransaccionPagoService {

    TransaccionPago registrarTransaccion(TransaccionPago transaccionPago);
    TransaccionPago obtenerTransaccionPorId(Long id);
    List<TransaccionPago> listarTransacciones();
}
