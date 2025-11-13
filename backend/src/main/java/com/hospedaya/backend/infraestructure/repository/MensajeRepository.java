package com.hospedaya.backend.infraestructure.repository;

import com.hospedaya.backend.domain.entity.Mensaje;
import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.entity.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    // Buscar mensajes entre dos usuarios para un alojamiento específico
    List<Mensaje> findByEmisorAndReceptorAndAlojamientoOrderByFechaEnvioAsc(
            Usuario emisor, Usuario receptor, Alojamiento alojamiento);
    
    // Buscar mensajes donde el usuario es emisor o receptor para un alojamiento específico
    @Query("SELECT m FROM Mensaje m WHERE " +
           "((m.emisor = :usuario AND m.receptor = :otroUsuario) OR " +
           "(m.emisor = :otroUsuario AND m.receptor = :usuario)) " +
           "AND m.alojamiento = :alojamiento " +
           "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversation(Usuario usuario, Usuario otroUsuario, Alojamiento alojamiento);
    
    // Contar mensajes no leídos para un usuario
    long countByReceptorAndLeidoFalse(Usuario receptor);
    
    // Buscar alojamientos con conversaciones para un usuario
    @Query("SELECT DISTINCT m.alojamiento FROM Mensaje m WHERE m.emisor = :usuario OR m.receptor = :usuario")
    List<Alojamiento> findAlojamientosWithConversations(Usuario usuario);
    
    // Buscar usuarios con los que un usuario ha conversado sobre un alojamiento específico
    @Query("SELECT DISTINCT CASE WHEN m.emisor = :usuario THEN m.receptor ELSE m.emisor END " +
           "FROM Mensaje m WHERE (m.emisor = :usuario OR m.receptor = :usuario) " +
           "AND m.alojamiento = :alojamiento")
    List<Usuario> findUsuariosInConversation(Usuario usuario, Alojamiento alojamiento);
}