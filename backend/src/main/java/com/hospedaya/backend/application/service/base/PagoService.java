package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Pago;

import java.util.List;

public interface PagoService {

    Pago registrarPago(Pago pago);
    Pago obtenerPagoPorId(Long id);
    List<Pago> listarPagos();
    void eliminarPago(Long id);
}
