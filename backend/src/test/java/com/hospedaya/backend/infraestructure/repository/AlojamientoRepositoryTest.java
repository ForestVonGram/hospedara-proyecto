package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.EstadoAlojamiento;
import com.hospedaya.backend.domain.enums.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AlojamientoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    private Usuario anfitrion1;
    private Usuario anfitrion2;
    private Alojamiento alojamiento1;
    private Alojamiento alojamiento2;
    private Alojamiento alojamiento3;

    @BeforeEach
    void setUp() {
        // Create anfitriones
        anfitrion1 = new Usuario();
        anfitrion1.setNombre("Anfitrion 1");
        anfitrion1.setEmail("anfitrion1@example.com");
        anfitrion1.setPassword("password123");
        anfitrion1.setRol(Rol.ANFITRION);
        anfitrion1.setFechaRegistro(LocalDate.now());
        anfitrion1.setActivo(true);
        entityManager.persist(anfitrion1);

        anfitrion2 = new Usuario();
        anfitrion2.setNombre("Anfitrion 2");
        anfitrion2.setEmail("anfitrion2@example.com");
        anfitrion2.setPassword("password123");
        anfitrion2.setRol(Rol.ANFITRION);
        anfitrion2.setFechaRegistro(LocalDate.now());
        anfitrion2.setActivo(true);
        entityManager.persist(anfitrion2);

        // Create alojamientos
        alojamiento1 = new Alojamiento();
        alojamiento1.setNombre("Casa de Playa");
        alojamiento1.setDescripcion("Hermosa casa frente al mar");
        alojamiento1.setDireccion("Calle Playa 123, Cartagena");
        alojamiento1.setPrecioPorNoche(100.0);
        alojamiento1.setEstado(EstadoAlojamiento.ACTIVO);
        alojamiento1.setAnfitrion(anfitrion1);
        entityManager.persist(alojamiento1);

        alojamiento2 = new Alojamiento();
        alojamiento2.setNombre("Apartamento en la Ciudad");
        alojamiento2.setDescripcion("Moderno apartamento en el centro");
        alojamiento2.setDireccion("Calle Centro 456, Bogotá");
        alojamiento2.setPrecioPorNoche(80.0);
        alojamiento2.setEstado(EstadoAlojamiento.ACTIVO);
        alojamiento2.setAnfitrion(anfitrion1);
        entityManager.persist(alojamiento2);

        alojamiento3 = new Alojamiento();
        alojamiento3.setNombre("Cabaña en la Montaña");
        alojamiento3.setDescripcion("Acogedora cabaña con vista a la montaña");
        alojamiento3.setDireccion("Calle Montaña 789, Medellín");
        alojamiento3.setPrecioPorNoche(120.0);
        alojamiento3.setEstado(EstadoAlojamiento.ACTIVO);
        alojamiento3.setAnfitrion(anfitrion2);
        entityManager.persist(alojamiento3);

        entityManager.flush();
    }

    @Test
    void whenFindByPrecioPorNocheBetween_thenReturnAlojamientos() {
        // when
        List<Alojamiento> result = alojamientoRepository.findByPrecioPorNocheBetween(70.0, 110.0);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Alojamiento::getNombre)
                .containsExactlyInAnyOrder("Casa de Playa", "Apartamento en la Ciudad");
    }

    @Test
    void whenFindByNombreContainingIgnoreCase_thenReturnAlojamientos() {
        // when
        List<Alojamiento> result = alojamientoRepository.findByNombreContainingIgnoreCase("casa");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Casa de Playa");
    }

    @Test
    void whenFindByDireccionContainingIgnoreCase_thenReturnAlojamientos() {
        // when
        List<Alojamiento> result = alojamientoRepository.findByDireccionContainingIgnoreCase("bogotá");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDireccion()).contains("Bogotá");
    }

    @Test
    void whenFindByAnfitrionId_thenReturnAlojamientos() {
        // when
        List<Alojamiento> result = alojamientoRepository.findByAnfitrionId(anfitrion1.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Alojamiento::getAnfitrion)
                .extracting(Usuario::getId)
                .containsOnly(anfitrion1.getId());
    }

    @Test
    void whenExistsByNombreAndAnfitrionId_thenReturnTrue() {
        // when
        boolean exists = alojamientoRepository.existsByNombreAndAnfitrionId(
                "Casa de Playa", anfitrion1.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByNombreAndAnfitrionIdWithNonExistingValues_thenReturnFalse() {
        // when
        boolean exists = alojamientoRepository.existsByNombreAndAnfitrionId(
                "Casa Inexistente", anfitrion1.getId());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void whenFindAllByOrderByPrecioPorNocheAsc_thenReturnAlojamientosOrderedByPriceAsc() {
        // when
        List<Alojamiento> result = alojamientoRepository.findAllByOrderByPrecioPorNocheAsc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Alojamiento::getPrecioPorNoche)
                .isSorted();
    }

    @Test
    void whenFindAllByOrderByPrecioPorNocheDesc_thenReturnAlojamientosOrderedByPriceDesc() {
        // when
        List<Alojamiento> result = alojamientoRepository.findAllByOrderByPrecioPorNocheDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Alojamiento::getPrecioPorNoche)
                .isSortedAccordingTo((p1, p2) -> Double.compare(p2, p1));
    }

    @Test
    void whenFindByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase_thenReturnAlojamientos() {
        // when
        List<Alojamiento> result = alojamientoRepository
                .findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase("montaña", "montaña");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Cabaña en la Montaña");
    }
}