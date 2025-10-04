package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Reserva;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.EstadoAlojamiento;
import com.hospedaya.backend.domain.enums.EstadoReserva;
import com.hospedaya.backend.domain.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReservaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservaRepository reservaRepository;

    private Usuario usuario;
    private Alojamiento alojamiento;
    private Reserva reserva1;
    private Reserva reserva2;

    @BeforeEach
    void setUp() {
        // Create usuario
        usuario = new Usuario();
        usuario.setNombre("Usuario Test");
        usuario.setEmail("usuario@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setActivo(true);
        entityManager.persist(usuario);

        // Create anfitrion
        Usuario anfitrion = new Usuario();
        anfitrion.setNombre("Anfitrion Test");
        anfitrion.setEmail("anfitrion@example.com");
        anfitrion.setPassword("password123");
        anfitrion.setRol(Rol.ANFITRION);
        anfitrion.setFechaRegistro(LocalDate.now());
        anfitrion.setActivo(true);
        entityManager.persist(anfitrion);

        // Create alojamiento
        alojamiento = new Alojamiento();
        alojamiento.setNombre("Alojamiento Test");
        alojamiento.setDescripcion("Descripción del alojamiento");
        alojamiento.setDireccion("Dirección del alojamiento");
        alojamiento.setPrecioPorNoche(100.0);
        alojamiento.setEstado(EstadoAlojamiento.ACTIVO);
        alojamiento.setAnfitrion(anfitrion);
        entityManager.persist(alojamiento);

        // Create reservas
        reserva1 = new Reserva();
        reserva1.setUsuario(usuario);
        reserva1.setAlojamiento(alojamiento);
        reserva1.setFechaInicio(LocalDate.now().plusDays(1));
        reserva1.setFechaFin(LocalDate.now().plusDays(5));
        reserva1.setEstado(EstadoReserva.PENDIENTE);
        entityManager.persist(reserva1);

        reserva2 = new Reserva();
        reserva2.setUsuario(usuario);
        reserva2.setAlojamiento(alojamiento);
        reserva2.setFechaInicio(LocalDate.now().plusDays(10));
        reserva2.setFechaFin(LocalDate.now().plusDays(15));
        reserva2.setEstado(EstadoReserva.CONFIRMADA);
        entityManager.persist(reserva2);

        entityManager.flush();
    }

    @Test
    void whenFindByUsuarioId_thenReturnReservas() {
        // when
        List<Reserva> found = reservaRepository.findByUsuarioId(usuario.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(reserva -> reserva.getUsuario().getId().equals(usuario.getId()));
    }

    @Test
    void whenFindByAlojamientoId_thenReturnReservas() {
        // when
        List<Reserva> found = reservaRepository.findByAlojamientoId(alojamiento.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(reserva -> reserva.getAlojamiento().getId().equals(alojamiento.getId()));
    }

    @Test
    void whenFindByEstado_thenReturnReservas() {
        // when
        List<Reserva> pendientes = reservaRepository.findByEstado(EstadoReserva.PENDIENTE);
        List<Reserva> confirmadas = reservaRepository.findByEstado(EstadoReserva.CONFIRMADA);

        // then
        assertThat(pendientes).hasSize(1);
        assertThat(pendientes.get(0).getEstado()).isEqualTo(EstadoReserva.PENDIENTE);

        assertThat(confirmadas).hasSize(1);
        assertThat(confirmadas.get(0).getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }

    @Test
    void whenFindByFechaInicioBetween_thenReturnReservas() {
        // when
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);
        List<Reserva> result = reservaRepository.findByFechaInicioBetween(start, end);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFechaInicio()).isAfterOrEqualTo(start);
        assertThat(result.get(0).getFechaInicio()).isBeforeOrEqualTo(end);
    }
}
