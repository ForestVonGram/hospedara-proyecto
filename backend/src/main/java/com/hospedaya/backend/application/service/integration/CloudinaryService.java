package com.hospedaya.backend.application.service.integration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Servicio para subir imágenes a Cloudinary
 */
@Service
public class CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
    
    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        // Limpiar espacios en blanco (evita fallas de firma por espacios accidentales)
        cloudName = cloudName == null ? "" : cloudName.trim();
        apiKey = apiKey == null ? "" : apiKey.trim();
        apiSecret = apiSecret == null ? "" : apiSecret.trim();

        // Configurar usando Map para evitar problemas de URL-encoding con caracteres especiales en el apiSecret
        // (por ejemplo: :, @, /, %, ?). Si se usa la URL cloudinary://... estos caracteres pueden romper el parseo
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));

        logger.info("✅ Cloudinary configurado para cloud: {}", cloudName);
        logger.info("🔑 Credenciales - Cloud: {}, API Key length: {}, API Secret length: {}",
                cloudName, apiKey.length(), apiSecret.length());
    }

    /**
     * Sube una imagen a Cloudinary
     * 
     * @param file Archivo a subir
     * @param folder Carpeta en Cloudinary (ej: "avatars", "alojamientos")
     * @return URL de la imagen subida
     */
    public String uploadImage(MultipartFile file, String folder) {
        try {
            logger.info("📤 Subiendo imagen a Cloudinary: {} ({})", file.getOriginalFilename(), folder);
            
            // Configuración simplificada sin transformaciones
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "hospedaya/" + folder,
                            "resource_type", "auto"
                    ));

            String url = (String) uploadResult.get("secure_url");
            logger.info("✅ Imagen subida exitosamente: {}", url);
            return url;
            
        } catch (IOException e) {
            logger.error("❌ Error al subir imagen a Cloudinary", e);
            logger.error("❌ Detalles del error: {}", e.toString());
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de Cloudinary
     * 
     * @param publicId ID público de la imagen en Cloudinary
     */
    public void deleteImage(String publicId) {
        try {
            logger.info("🗑️ Eliminando imagen de Cloudinary: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("✅ Imagen eliminada exitosamente");
        } catch (IOException e) {
            logger.error("❌ Error al eliminar imagen de Cloudinary", e);
            // No lanzar excepción para no bloquear otras operaciones
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary
     * 
     * @param url URL de Cloudinary
     * @return Public ID o null si no es una URL válida
     */
    public String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }
        
        try {
            // URL ejemplo: https://res.cloudinary.com/demo/image/upload/v1234/hospedaya/avatars/image.jpg
            String[] parts = url.split("/upload/");
            if (parts.length < 2) return null;
            
            String path = parts[1];
            // Remover versión (v1234/) si existe
            path = path.replaceFirst("v\\d+/", "");
            // Remover extensión
            path = path.replaceFirst("\\.[^.]+$", "");
            
            return path;
        } catch (Exception e) {
            logger.warn("⚠️ No se pudo extraer public_id de URL: {}", url);
            return null;
        }
    }
}
