package com.hospedaya.backend.presentation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador temporal para verificar configuración de Cloudinary
 * ELIMINAR EN PRODUCCIÓN
 */
@RestController
@RequestMapping("/test")
public class CloudinaryTestController {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    
    @Value("${cloudinary.api-key}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @GetMapping("/cloudinary-config")
    public Map<String, String> getCloudinaryConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud-name", cloudName);
        config.put("api-key", apiKey);
        config.put("api-secret-length", String.valueOf(apiSecret.length()));
        config.put("api-secret-first-4", apiSecret.substring(0, Math.min(4, apiSecret.length())));
        config.put("api-secret-last-4", apiSecret.substring(Math.max(0, apiSecret.length() - 4)));
        return config;
    }
}
