package com.hospedaya.backend.application.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hospedaya.backend.application.dto.imagen.ImageUploadResponse;
import com.hospedaya.backend.application.service.base.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
public class CloudinaryImageStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:hospedaya}")
    private String baseFolder;

    // 10 MB default
    @Value("${upload.images.max-size-bytes:10485760}")
    private long maxSizeBytes;

    public CloudinaryImageStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file, String folder) {
        validate(file);
        String targetFolder = buildFolder(folder);
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", targetFolder,
                    "resource_type", "image",
                    "unique_filename", true,
                    "overwrite", false
            ));

            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            String format = (String) uploadResult.get("format");
            Integer width = (Integer) uploadResult.get("width");
            Integer height = (Integer) uploadResult.get("height");
            Number bytes = (Number) uploadResult.get("bytes");

            return ImageUploadResponse.builder()
                    .url(secureUrl)
                    .publicId(publicId)
                    .format(format)
                    .width(width != null ? width : 0)
                    .height(height != null ? height : 0)
                    .bytes(bytes != null ? bytes.longValue() : file.getSize())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Error subiendo imagen a Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (IOException e) {
            throw new RuntimeException("Error eliminando imagen en Cloudinary: " + e.getMessage(), e);
        }
    }

    private String buildFolder(String folder) {
        if (folder == null || folder.isBlank()) return baseFolder;
        return baseFolder + "/" + folder;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo es obligatorio");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("La imagen supera el tamaño máximo permitido");
        }
        // Permitidos: image/jpeg, image/png, image/webp, image/gif
        String contentType = file.getContentType();
        if (contentType == null || Arrays.stream(new String[]{
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_PNG_VALUE,
                "image/webp",
                MediaType.IMAGE_GIF_VALUE
        }).noneMatch(contentType::equalsIgnoreCase)) {
            throw new IllegalArgumentException("Formato de imagen no soportado");
        }
    }
}
