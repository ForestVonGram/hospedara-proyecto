package com.hospedaya.backend.application.service.impl;

import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.domain.enums.Rol;
import com.hospedaya.backend.exception.ResourceNotFoundException;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");
        usuario.setRol(Rol.USUARIO);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setActivo(true);
    }

    @Test
    void whenCrearUsuario_thenReturnUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario created = usuarioService.crearUsuario(usuario);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getNombre()).isEqualTo("Test User");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void whenActualizarUsuario_thenReturnUpdatedUsuario() {
        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setNombre("Updated User");
        updatedUsuario.setEmail("updated@example.com");
        updatedUsuario.setPassword("newpassword");
        updatedUsuario.setTelefono("1234567890");
        updatedUsuario.setRol(Rol.ANFITRION);

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.actualizarUsuario(1L, updatedUsuario);

        assertThat(result).isNotNull();
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void whenActualizarUsuarioWithNonExistingId_thenThrowException() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.actualizarUsuario(999L, usuario);
        });

        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void whenEliminarUsuario_thenRepositoryMethodCalled() {
        when(usuarioRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(anyLong());

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenEliminarUsuarioWithNonExistingId_thenThrowException() {
        when(usuarioRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.eliminarUsuario(999L);
        });

        verify(usuarioRepository, times(1)).existsById(999L);
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void whenFindById_thenReturnUsuario() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));

        Usuario found = usuarioService.findById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void whenFindByIdWithNonExistingId_thenThrowException() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.findById(999L);
        });

        verify(usuarioRepository, times(1)).findById(999L);
    }

    @Test
    void whenListarUsuarios_thenReturnAllUsuarios() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Another User");
        usuario2.setEmail("another@example.com");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        List<Usuario> usuarios = usuarioService.listarUsuarios();

        assertThat(usuarios).isNotNull();
        assertThat(usuarios.size()).isEqualTo(2);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void whenAsignarRol_thenReturnUsuarioWithUpdatedRol() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.asignarRol(1L, Rol.ANFITRION);

        assertThat(result).isNotNull();
        assertThat(result.getRol()).isEqualTo(Rol.ANFITRION);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void whenAsignarRolWithNonExistingId_thenThrowException() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.asignarRol(999L, Rol.ANFITRION);
        });

        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}