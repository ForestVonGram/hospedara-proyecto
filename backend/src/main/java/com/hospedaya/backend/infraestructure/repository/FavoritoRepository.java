package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Alojamiento;
import com.hospedaya.backend.domain.entity.Favorito;
import com.hospedaya.backend.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    Optional<Favorito> findByUsuario(Usuario usuario);

    Optional<Favorito> findByAlojamiento(Alojamiento alojamiento);

    Optional<Favorito> findByUsuarioAndAlojamiento(Usuario usuario, Alojamiento alojamiento);

    List<Favorito> findAllByUsuario(Usuario usuario);
}
