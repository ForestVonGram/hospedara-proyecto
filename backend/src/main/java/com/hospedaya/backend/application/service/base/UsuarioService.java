package com.hospedaya.backend.application.service.base;

import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;

import java.util.List;

public interface UsuarioService {

    Usuario crearUsuario(Usuario usuario);
    Usuario actualizarUsuario(Long id, Usuario usuario);
    void eliminarUsuario(Long idUsuario);
    Usuario findById(Long idUsuario);
    List<Usuario> listarUsuarios();
    Usuario asignarRol(Long idUsuario, Rol nuevoRol);
    Usuario activarUsuario(Long idUsuario);
    Usuario desactivarUsuario(Long idUsuario);

    /**
     * Cancela todas las reservas del usuario (si aplica) y luego elimina definitivamente la cuenta.
     * Pensado para el flujo de "eliminar mi cuenta".
     */
    void cancelarReservasYEliminarUsuario(Long idUsuario);
}
