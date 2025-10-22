package com.hospedaya.backend.application.service.integration;

import com.hospedaya.backend.application.service.base.PagoService;
import com.hospedaya.backend.application.service.base.TransaccionPagoService;
import com.hospedaya.backend.domain.entity.Pago;
import com.hospedaya.backend.domain.entity.TransaccionPago;
import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.enums.EstadoPago;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import com.hospedaya.backend.exception.BadRequestException;
import com.hospedaya.backend.infraestructure.repository.ReservaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class MercadoPagoService {

    private final PagoService pagoService;
    private final TransaccionPagoService transaccionPagoService;
    private final ReservaRepository reservaRepository;

    @Value("${mercadopago.access-token:}")
    private String accessToken;
    @Value("${mercadopago.success-url:https://example.com/success}")
    private String successUrl;
    @Value("${mercadopago.pending-url:https://example.com/pending}")
    private String pendingUrl;
    @Value("${mercadopago.failure-url:https://example.com/failure}")
    private String failureUrl;

    public MercadoPagoService(PagoService pagoService, TransaccionPagoService transaccionPagoService, ReservaRepository reservaRepository) {
        this.pagoService = pagoService;
        this.transaccionPagoService = transaccionPagoService;
        this.reservaRepository = reservaRepository;
    }

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank()) {
            // No impide levantar la app, pero cualquier intento de uso sin token debe fallar claro
            return;
        }
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    /**
     * Crea una preferencia en Mercado Pago y guarda el ID en el pago.
     * Devuelve la URL init_point para redirigir al checkout.
     */
    public String crearPreferenciaParaPago(Long pagoId) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new BadRequestException("Mercado Pago no configurado: falta access-token");
        }
        Pago pago = pagoService.obtenerPagoPorId(pagoId);

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title("Pago reserva " + pago.getReserva().getId())
                .quantity(1)
                .unitPrice(pago.getMonto())
                .currencyId("COP") // Ajusta si usas otra moneda
                .build();

        String externalRef = pago.getReferenciaExterna(); // ya es único en la entidad

        PreferenceRequest request = PreferenceRequest.builder()
                .items(Collections.singletonList(item))
                .externalReference(externalRef)
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success(successUrl)
                        .pending(pendingUrl)
                        .failure(failureUrl)
                        .build())
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference;
        try {
            preference = client.create(request);
        } catch (com.mercadopago.exceptions.MPException | com.mercadopago.exceptions.MPApiException e) {
            throw new BadRequestException("Error al crear preferencia en Mercado Pago: " + e.getMessage());
        }

        pago.setPreferenciaId(preference.getId());
        // Estado pasa a pendiente hasta confirmación de webhook
        pago.setEstado(EstadoPago.PENDIENTE);
        pagoService.actualizarPago(pago);

        return preference.getInitPoint();
    }

    /**
     * Procesa notificación de pago consultando a Mercado Pago por el paymentId
     */
    public void procesarNotificacionPago(String paymentId, String topic, String rawBody) {
        if (paymentId == null || paymentId.isBlank()) return;
        PaymentClient paymentClient = new PaymentClient();
        Payment payment;
        try {
            payment = paymentClient.get(Long.parseLong(paymentId));
        } catch (com.mercadopago.exceptions.MPException | com.mercadopago.exceptions.MPApiException e) {
            throw new BadRequestException("Error al consultar pago en Mercado Pago: " + e.getMessage());
        }

        String externalRef = payment.getExternalReference();
        // Buscar el pago por referencia externa a través del PagoService
        Pago pago = pagoService.obtenerPagoPorReferencia(externalRef);

        EstadoPago nuevoEstado = mapEstado(payment.getStatus());
        pago.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPago.APROBADO) {
            // marcar confirmación
            pago.setFechaConfirmacion(java.time.LocalDateTime.now());
            // actualizar estado de la reserva a PAGADA
            Reserva reserva = pago.getReserva();
            if (reserva != null) {
                reserva.setEstado(EstadoReserva.PAGADA);
                reservaRepository.save(reserva);
            }
        }
        pagoService.actualizarPago(pago);

        // Registrar transacción (idempotencia simple: no se maneja aún duplicado exacto)
        TransaccionPago tx = new TransaccionPago();
        tx.setPago(pago);
        // SDK suele devolver BigDecimal, en caso contrario toString asegura precisión
        java.math.BigDecimal txAmount = (payment.getTransactionAmount() instanceof java.math.BigDecimal)
                ? (java.math.BigDecimal) payment.getTransactionAmount()
                : new java.math.BigDecimal(String.valueOf(payment.getTransactionAmount()));
        tx.setMonto(txAmount);
        tx.setEstadoPago(nuevoEstado);
        tx.setReferenciaExterna(String.valueOf(payment.getId()));
        // Guardar hasta 500 chars del payload si excede
        String detalle = rawBody == null ? "" : rawBody;
        if (detalle.length() > 500) detalle = detalle.substring(0, 500);
        tx.setDetalle(detalle);
        transaccionPagoService.registrarTransaccion(tx);
    }

    private EstadoPago mapEstado(String mpStatus) {
        if (mpStatus == null) return EstadoPago.PENDIENTE;
        return switch (mpStatus) {
            case "approved" -> EstadoPago.APROBADO;
            case "in_process" -> EstadoPago.PENDIENTE;
            case "rejected" -> EstadoPago.RECHAZADO;
            case "cancelled" -> EstadoPago.RECHAZADO;
            case "refunded" -> EstadoPago.REEMBOLSADO;
            default -> EstadoPago.PENDIENTE;
        };
    }
}
