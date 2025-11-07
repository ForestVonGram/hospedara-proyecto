package com.hospedaya.backend.presentation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retorno/mp")
public class MpReturnController {

    @Value("${frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    @GetMapping("/success")
    public ResponseEntity<Void> success(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalRef,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "collection_status", required = false) String collectionStatus) {
        String finalStatus = status != null ? status : (collectionStatus != null ? collectionStatus : "approved");
        String url = String.format("%s/pago-correcto?payment_id=%s&status=%s&external_reference=%s",
                trimSlash(frontendBaseUrl),
                encode(paymentId),
                encode(finalStatus),
                encode(externalRef));
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    @GetMapping("/pending")
    public ResponseEntity<Void> pending(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalRef,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "collection_status", required = false) String collectionStatus) {
        String finalStatus = status != null ? status : (collectionStatus != null ? collectionStatus : "in_process");
        String url = String.format("%s/pago-incorrecto?payment_id=%s&status=%s&external_reference=%s",
                trimSlash(frontendBaseUrl),
                encode(paymentId),
                encode(finalStatus),
                encode(externalRef));
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    @GetMapping("/failure")
    public ResponseEntity<Void> failure(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalRef,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "collection_status", required = false) String collectionStatus,
            @RequestParam(name = "reason", required = false) String reason) {
        String finalStatus = status != null ? status : (collectionStatus != null ? collectionStatus : "rejected");
        String url = String.format("%s/pago-incorrecto?payment_id=%s&status=%s&external_reference=%s%s",
                trimSlash(frontendBaseUrl),
                encode(paymentId),
                encode(finalStatus),
                encode(externalRef),
                reason != null ? "&reason=" + encode(reason) : "");
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    private static String trimSlash(String base) {
        if (base == null) return "";
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    private static String encode(String v) {
        if (v == null) return "";
        try {
            return java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return v;
        }
    }
}
