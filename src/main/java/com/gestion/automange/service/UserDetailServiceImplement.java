package com.gestion.automange.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gestion.automange.model.Usuario;

@Service
public class UserDetailServiceImplement implements UserDetailsService {

    @Autowired
    private IUsuarioService usuarioService;


    private Logger log = LoggerFactory.getLogger(UserDetailServiceImplement.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Buscando usuario con email: {}", email);
        Optional<Usuario> optionalUser = usuarioService.findByEmail(email);

        if (optionalUser.isEmpty()) {
            log.warn("Usuario no encontrado con email: {}", email);
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        Usuario usuario = optionalUser.get();
        log.info("Usuario encontrado: {}", usuario.getEmail());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword()) // Asegúrate de que esté encriptado con BCrypt
                .authorities(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().toUpperCase())) // Prefijo "ROLE_"
                .build();
    }

}
