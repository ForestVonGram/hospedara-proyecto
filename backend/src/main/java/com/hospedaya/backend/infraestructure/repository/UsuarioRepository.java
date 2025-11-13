package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    // Búsquedas insensibles a mayúsculas/minúsculas para email
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByNombre(String nombre);
}
