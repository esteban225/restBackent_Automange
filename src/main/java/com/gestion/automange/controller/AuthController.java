package com.gestion.automange.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.gestion.automange.config.JwtProvider;
import com.gestion.automange.dto.AuthRequest;
import com.gestion.automange.dto.AuthResponse;
import com.gestion.automange.dto.RegisterRequest;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.IUsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite solicitudes desde cualquier origen (ajusta según necesidad)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final IUsuarioService usuarioService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider, 
                          UserDetailsService userDetailsService, IUsuarioService usuarioService,
                          BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtProvider.generateToken(userDetails);
        return new AuthResponse(token);
    }

    @PostMapping("/register")
	public AuthResponse register(@RequestBody RegisterRequest request) {
        // Verificar si el usuario ya existe
        if (usuarioService.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso.");
        }

        // Crear un nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        nuevoUsuario.setTipo(request.getTipo()); // Asignar rol (ej. "ADMIN" o "USER")

        // Guardar usuario en la base de datos
        usuarioService.save(nuevoUsuario);

        // Generar JWT para el usuario registrado
        UserDetails userDetails = userDetailsService.loadUserByUsername(nuevoUsuario.getEmail());
        String token = jwtProvider.generateToken(userDetails);

        return new AuthResponse(token);
    }
}
