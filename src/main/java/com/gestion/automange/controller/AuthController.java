package com.gestion.automange.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

			UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
			String token = jwtProvider.generateToken(userDetails);

			// Retorna éxito con token
			return ResponseEntity.ok(
					Map.of("status", "success", "code", 200, "message", "Inicio de sesión exitoso.", "token", token));

		} catch (Exception e) {
			// Retorna error con código 401 (Unauthorized)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "code", 401, "message",
					"Credenciales incorrectas. Verifica tu correo y contraseña."));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterRequest request) {
		Map<String, Object> response = new HashMap<>();

		// Validar si algún campo es nulo o vacío
		if (request.getNombre() == null || request.getNombre().isEmpty() || request.getUsername() == null
				|| request.getUsername().isEmpty() || request.getEmail() == null || request.getEmail().isEmpty()
				|| request.getPassword() == null || request.getPassword().isEmpty() || request.getTelefono() == null
				|| request.getTelefono().isEmpty() || request.getDireccion() == null || request.getDireccion().isEmpty()
				|| request.getTipo() == null || request.getTipo().isEmpty()) {

			response.put("status", "error");
			response.put("code", 400);
			response.put("message", "Todos los campos son obligatorios.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Validar si el correo ya está registrado
		if (usuarioService.findByEmail(request.getEmail()).isPresent()) {
			response.put("status", "error");
			response.put("code", 409);
			response.put("message", "El correo ya está registrado.");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

		// Crear usuario
		Usuario usuario = new Usuario();
		usuario.setNombre(request.getNombre());
		usuario.setUsername(request.getUsername());
		usuario.setEmail(request.getEmail());
		usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
		usuario.setTelefono(request.getTelefono());
		usuario.setDireccion(request.getDireccion());
		usuario.setTipo(request.getTipo());

		// Guardar en BD
		usuarioService.save(usuario);

		response.put("status", "success");
		response.put("code", 201);
		response.put("message", "Usuario registrado exitosamente.");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
