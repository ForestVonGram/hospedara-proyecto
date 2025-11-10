package com.hospedaya.backend.infraestructure.config;

import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * Inicializador de datos para crear usuarios de prueba en la base de datos.
 * Solo se ejecuta si los usuarios no existen.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    // Cambiar a true si quieres deshabilitar la creación de usuarios de prueba
    private static final boolean DISABLE_TEST_USERS = false;

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (DISABLE_TEST_USERS) {
                logger.info("ℹ️  Creación de usuarios de prueba deshabilitada");
                return;
            }
            
            logger.info("🔍 Verificando usuarios de prueba...");

            // Usuario Huésped de prueba
            if (!usuarioRepository.existsByEmail("huesped@test.com")) {
                Usuario huesped = new Usuario();
                huesped.setNombre("Usuario Huésped");
                huesped.setEmail("huesped@test.com");
                huesped.setPassword(passwordEncoder.encode("123456"));
                huesped.setRol(Rol.USUARIO);
                huesped.setTelefono("3001234567");
                huesped.setActivo(true);
                huesped.setFechaRegistro(LocalDate.now());
                usuarioRepository.save(huesped);
                logger.info("✅ Usuario huésped creado - Email: huesped@test.com | Password: 123456");
            } else {
                logger.info("ℹ️  Usuario huésped ya existe - Email: huesped@test.com");
            }

            // Usuario Anfitrión de prueba
            if (!usuarioRepository.existsByEmail("anfitrion@test.com")) {
                Usuario anfitrion = new Usuario();
                anfitrion.setNombre("Usuario Anfitrión");
                anfitrion.setEmail("anfitrion@test.com");
                anfitrion.setPassword(passwordEncoder.encode("123456"));
                anfitrion.setRol(Rol.ANFITRION);
                anfitrion.setTelefono("3007654321");
                anfitrion.setActivo(true);
                anfitrion.setFechaRegistro(LocalDate.now());
                usuarioRepository.save(anfitrion);
                logger.info("✅ Usuario anfitrión creado - Email: anfitrion@test.com | Password: 123456");
            } else {
                logger.info("ℹ️  Usuario anfitrión ya existe - Email: anfitrion@test.com");
            }

            // Usuario Admin de prueba (opcional)
            if (!usuarioRepository.existsByEmail("admin@test.com")) {
                Usuario admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setEmail("admin@test.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol(Rol.ADMIN);
                admin.setTelefono("3009876543");
                admin.setActivo(true);
                admin.setFechaRegistro(LocalDate.now());
                usuarioRepository.save(admin);
                logger.info("✅ Usuario admin creado - Email: admin@test.com | Password: admin123");
            } else {
                logger.info("ℹ️  Usuario admin ya existe - Email: admin@test.com");
            }

            logger.info("✨ Inicialización de datos completada");
            logger.info("═══════════════════════════════════════════════════════");
            logger.info("📋 CREDENCIALES DE PRUEBA:");
            logger.info("   HUÉSPED:   huesped@test.com   / 123456");
            logger.info("   ANFITRIÓN: anfitrion@test.com / 123456");
            logger.info("   ADMIN:     admin@test.com     / admin123");
            logger.info("═══════════════════════════════════════════════════════");
        };
    }
}
