package com.hospedaya.backend.infraestructure.security;

import com.hospedaya.backend.domain.entity.Usuario;
import com.hospedaya.backend.infraestructure.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalized = username != null ? username.trim().toLowerCase() : null;
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + (usuario.getRol() != null ? usuario.getRol().name() : "USER"))
        );

        boolean enabled = usuario.getActivo() != null ? usuario.getActivo() : true;
        boolean accountNonLocked = usuario.getAccountLockedUntil() == null
                || usuario.getAccountLockedUntil().isBefore(LocalDateTime.now());

        return new User(usuario.getEmail(), usuario.getPassword(), enabled,
                true, true, accountNonLocked, authorities);
    }
}
