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

    @Test
    void whenCrearUsuario_repositoryThrows_thenPropagate() {
        RuntimeException boom = new RuntimeException("DB error");
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(boom);
        assertThrows(RuntimeException.class, () -> usuarioService.crearUsuario(usuario));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void whenCrearUsuario_withNullEntity_thenThrowValidationException() {
        assertThrows(com.hospedaya.backend.exception.ValidationException.class, () -> usuarioService.crearUsuario(null));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void whenActualizarUsuario_shouldUpdateAllFieldsBeforeSave() {
        Usuario update = new Usuario();
        update.setNombre("Nuevo Nombre");
        update.setEmail("nuevo@example.com");
        update.setPassword("pass");
        update.setTelefono("111");
        update.setRol(Rol.ANFITRION);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.actualizarUsuario(1L, update);

        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(u ->
                u.getNombre().equals("Nuevo Nombre") &&
                        u.getEmail().equals("nuevo@example.com") &&
                        u.getPassword().equals("pass") &&
                        u.getTelefono().equals("111") &&
                        u.getRol() == Rol.ANFITRION
        ));
    }

    @Test
    void whenEliminarUsuario_deleteFails_thenPropagate() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("delete fail")).when(usuarioRepository).deleteById(1L);
        assertThrows(RuntimeException.class, () -> usuarioService.eliminarUsuario(1L));
        verify(usuarioRepository).existsById(1L);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void whenFindById_withNullId_thenThrowNPE() {
        // Mockito por defecto retorna Optional.empty() para métodos que devuelven Optional,
        // por lo que el servicio lanzará ResourceNotFoundException.
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.findById(null));
    }

    @Test
    void whenListarUsuarios_empty_thenReturnEmptyList() {
        when(usuarioRepository.findAll()).thenReturn(List.of());
        List<Usuario> result = usuarioService.listarUsuarios();
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(usuarioRepository).findAll();
    }

    @Test
    void whenListarUsuarios_single_thenReturnOne() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        List<Usuario> result = usuarioService.listarUsuarios();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(usuarioRepository).findAll();
    }

    @Test
    void whenAsignarRol_shouldSaveWithNewRol() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.asignarRol(1L, Rol.ANFITRION);

        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(u -> u.getRol() == Rol.ANFITRION));
    }
}