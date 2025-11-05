package com.hospedaya.backend.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve archivos estáticos desde el directorio local "uploads/"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:uploads/",           // si el working dir es backend/
                        "file:backend/uploads/",   // si el working dir es el root del repo
                        "classpath:/static/uploads/" // fallback opcional
                );
    }
}
