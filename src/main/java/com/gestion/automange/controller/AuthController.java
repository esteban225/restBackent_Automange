package com.gestion.automange.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.automange.config.JwtProvider;
import com.gestion.automange.dto.AuthRequest;
import com.gestion.automange.dto.RegisterRequest;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.EmailNotificationService;
import com.gestion.automange.service.IUsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");

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
			List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());

			String token = jwtProvider.generateToken(userDetails.getUsername(), roles);

			return ResponseEntity.ok(Map.of("status", "success", "code", 200, "message", "Inicio de sesión exitoso.",
					"token", token));

		} catch (BadCredentialsException e) {
			LOGGER.warn("Credenciales incorrectas: {}", request.getEmail());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "code", 401,
					"message", "Credenciales incorrectas. Verifica tu correo y contraseña."));
		} catch (Exception e) {
			LOGGER.error("Error al autenticar usuario", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "code", 500, "message", "Error interno del servidor."));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
		Map<String, Object> response = new HashMap<>();

		if (!validarRegistro(request)) {
			response.put("status", "error");
			response.put("code", 400);
			response.put("message", "Todos los campos son obligatorios y deben tener un formato válido.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		if (usuarioService.findByEmail(request.getEmail()).isPresent()) {
			response.put("status", "error");
			response.put("code", 409);
			response.put("message", "El correo ya está registrado.");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

		Usuario usuario = new Usuario();
		usuario.setNombre(request.getNombre());
		usuario.setUsername(request.getUsername());
		usuario.setEmail(request.getEmail());
		usuario.setPassword(passwordEncoder.encode(request.getPassword()));
		usuario.setTelefono(request.getTelefono());
		usuario.setDireccion(request.getDireccion());
		usuario.setRole("USER");

		usuarioService.save(usuario);
		emailNotificationService.sendRegistrationSuccessEmail(usuario.getEmail(), usuario.getNombre());

		response.put("status", "success");
		response.put("code", 201);
		response.put("message", "Usuario registrado exitosamente.");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	private boolean validarRegistro(RegisterRequest request) {
		return request.getNombre() != null && !request.getNombre().isEmpty() &&
				request.getUsername() != null && !request.getUsername().isEmpty() &&
				request.getEmail() != null && EMAIL_PATTERN.matcher(request.getEmail()).matches() &&
				request.getPassword() != null && request.getPassword().length() >= 6 &&
				request.getTelefono() != null && request.getTelefono().length() >= 10 &&
				request.getDireccion() != null && !request.getDireccion().isEmpty();
	}
}
