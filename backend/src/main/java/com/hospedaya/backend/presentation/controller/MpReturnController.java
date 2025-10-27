package com.hospedaya.backend.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retorno/mp")
public class MpReturnController {

    @GetMapping("/success")
    public ResponseEntity<String> success(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalRef,
            @RequestParam(name = "status", required = false) String status) {
        String msg = "Pago aprobado. Ref: " + (externalRef != null ? externalRef : "-")
                + ", payment_id: " + (paymentId != null ? paymentId : "-")
                + ", status: " + (status != null ? status : "-");
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/pending")
    public ResponseEntity<String> pending(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalRef,
            @RequestParam(name = "status", required = false) String status) {
        String msg = "Pago pendiente. Ref: " + (externalRef != null ? externalRef : "-")
                + ", payment_id: " + (paymentId != null ? paymentId : "-")
                + ", status: " + (status != null ? status : "-");
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/failure")
    public ResponseEntity<String> failure(
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam(name = "external_reference", required = false) String externalRef,
            @RequestParam(name = "status", required = false) String status) {
        String msg = "Pago fallido. Ref: " + (externalRef != null ? externalRef : "-")
                + ", payment_id: " + (paymentId != null ? paymentId : "-")
                + ", status: " + (status != null ? status : "-");
        return ResponseEntity.ok(msg);
    }
}
