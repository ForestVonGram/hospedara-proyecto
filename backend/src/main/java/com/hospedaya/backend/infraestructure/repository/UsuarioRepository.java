package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
