package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Notificacion;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByUsuario(Usuario usuario);

    List<Notificacion> findByTipo(TipoNotificacion tipo);

    List<Notificacion> findByLeida(boolean leida);

    List<Notificacion> findByFechaCreacion(LocalDateTime fechaCreacion);
}
