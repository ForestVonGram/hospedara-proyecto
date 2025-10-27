package com.hospedaya.backend.presentation.controller;

import com.hospedaya.backend.application.service.integration.MercadoPagoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/webhooks/mercadopago")
public class MercadoPagoWebhookController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoWebhookController.class);

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

        // Si no vienen query params, intenta extraer desde el body (formato v1 de MP)
        String bodyType = null;
        String bodyPaymentId = null;
        if ((effectiveTopic == null || effectiveTopic.isBlank()) || (dataId == null || dataId.isBlank())) {
            if (body != null && !body.isBlank()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(body);
                    // MP envía { type: "payment", action: "payment.updated", data: { id: "123" }, ... }
                    if (root.hasNonNull("type")) bodyType = root.get("type").asText();
                    if (root.hasNonNull("action") && bodyType == null) {
                        String action = root.get("action").asText("");
                        if (action.startsWith("payment")) bodyType = "payment";
                    }
                    JsonNode dataNode = root.get("data");
                    if (dataNode != null && dataNode.hasNonNull("id")) {
                        bodyPaymentId = dataNode.get("id").asText();
                    }
                } catch (Exception ex) {
                    log.warn("No se pudo parsear el body del webhook de MP: {}", ex.getMessage());
                }
            }
        }

        String finalTopic = effectiveTopic != null ? effectiveTopic : bodyType;
        String finalPaymentId = (dataId != null && !dataId.isBlank()) ? dataId : bodyPaymentId;

        if ("payment".equalsIgnoreCase(finalTopic) && finalPaymentId != null && !finalPaymentId.isBlank()) {
            try {
                mpService.procesarNotificacionPago(finalPaymentId, finalTopic, body);
            } catch (Exception e) {
                // No propagamos error al caller (Mercado Pago necesita 200 OK para considerar recibido)
                log.warn("Error procesando webhook de MP para payment_id {}: {}", finalPaymentId, e.getMessage());
            }
        }
        return ResponseEntity.ok().build();
    }
}
