package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.service.integration.MercadoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/webhooks/mercadopago")
public class MercadoPagoWebhookController {

    private final MercadoPagoService mpService;

    public MercadoPagoWebhookController(MercadoPagoService mpService) {
        this.mpService = mpService;
    }

    @PostMapping
    public ResponseEntity<Void> onEvent(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestBody(required = false) String body,
            HttpServletRequest request) {
        String effectiveTopic = topic != null ? topic : type;
        if ("payment".equalsIgnoreCase(effectiveTopic) && dataId != null) {
            mpService.procesarNotificacionPago(dataId, effectiveTopic, body);
        }
        return ResponseEntity.ok().build();
    }
}
