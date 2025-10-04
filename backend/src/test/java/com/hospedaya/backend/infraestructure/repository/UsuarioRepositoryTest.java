package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void whenFindByEmail_thenReturnUsuario() {
        // given
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setActivo(true);
        
        entityManager.persist(usuario);
        entityManager.flush();

        // when
        Optional<Usuario> found = usuarioRepository.findByEmail(usuario.getEmail());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(usuario.getEmail());
    }

    @Test
    public void whenFindByNonExistentEmail_thenReturnEmpty() {
        // when
        Optional<Usuario> found = usuarioRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    public void whenExistsByEmail_thenReturnTrue() {
        // given
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("exists@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setActivo(true);
        
        entityManager.persist(usuario);
        entityManager.flush();

        // when
        boolean exists = usuarioRepository.existsByEmail(usuario.getEmail());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsByNonExistentEmail_thenReturnFalse() {
        // when
        boolean exists = usuarioRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    public void whenFindByNombre_thenReturnUsuario() {
        // given
        Usuario usuario = new Usuario();
        usuario.setNombre("Unique Name");
        usuario.setEmail("unique@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setActivo(true);
        
        entityManager.persist(usuario);
        entityManager.flush();

        // when
        Optional<Usuario> found = usuarioRepository.findByNombre(usuario.getNombre());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo(usuario.getNombre());
    }

    @Test
    public void whenFindByNonExistentNombre_thenReturnEmpty() {
        // when
        Optional<Usuario> found = usuarioRepository.findByNombre("Nonexistent Name");

        // then
        assertThat(found).isEmpty();
    }
}