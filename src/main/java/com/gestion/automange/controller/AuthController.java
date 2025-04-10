package com.gestion.automange.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.gestion.automange.dto.RegisterRequest;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.EmailNotificationService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.service.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private EmailNotificationService emailNotificationService;

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
				|| request.getTelefono().isEmpty() || request.getDireccion() == null
				|| request.getDireccion().isEmpty()) {

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
		usuario.setTipo("USER");

		// Guardar en BD
		usuarioService.save(usuario);

		// Enviar notificación al correo
		emailNotificationService.sendRegistrationSuccessEmail(usuario.getEmail(), usuario.getNombre());

		response.put("status", "success");
		response.put("code", 201);
		response.put("message", "Usuario registrado exitosamente.");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Autowired
	private PasswordResetService passwordResetService;

	@PostMapping("/forgot-password")
	public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
		try {
			String email = request.get("email");

			if (email == null || email.isBlank()) {
				return ResponseEntity.badRequest().body(Map.of("error", "El email es obligatorio"));
			}

			passwordResetService.sendResetEmail(email);
			return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado"));

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", "No se encontró una cuenta con ese correo"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Ocurrió un error al enviar el correo"));
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
		try {
			// Validaciones básicas
			String token = request.get("token");
			String newPassword = request.get("newPassword");

			if (token == null || token.isEmpty()) {
				return ResponseEntity.badRequest().body("El token es requerido.");
			}
			if (newPassword == null || newPassword.length() < 6) {
				return ResponseEntity.badRequest().body("La nueva contraseña debe tener al menos 6 caracteres.");
			}

			// Llamar al servicio para actualizar la contraseña
			passwordResetService.resetPassword(token, newPassword);

			return ResponseEntity.ok("Contraseña actualizada con éxito.");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
		}
	}

}
