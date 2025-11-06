package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.application.dto.imagen.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    ImageUploadResponse upload(MultipartFile file, String folder);

    void delete(String publicId);
}
