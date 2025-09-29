package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {

    List<Alojamiento> findByPrecioPorNocheBetween(Double min, Double max);

    List<Alojamiento> findByNombreContainingIgnoreCase(String nombre);

    List<Alojamiento> findByDireccionContainingIgnoreCase(String direccion);

    List<Alojamiento> findByAnfitrionId(Long anfitrionId);

    boolean existsByNombreAndAnfitrionId(String nombre, Long anfitrionId);

    //Búsqueda por precio ordenado.
    List<Alojamiento> findAllByOrderByPrecioPorNocheAsc();
    List<Alojamiento> findAllByOrderByPrecioPorNocheDesc();

    List<Alojamiento> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);
}
