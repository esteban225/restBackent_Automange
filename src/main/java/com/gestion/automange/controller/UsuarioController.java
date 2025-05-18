package com.gestion.automange.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IUsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);
	private final IUsuarioService usuarioService;
	private final IOrdenService ordenService;
	private final BCryptPasswordEncoder passwordEncoder;

	public UsuarioController(IUsuarioService usuarioService, IOrdenService ordenService,
			BCryptPasswordEncoder passwordEncoder) {
		this.usuarioService = usuarioService;
		this.ordenService = ordenService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/detalles")
	public ResponseEntity<Map<String, Object>> getAllUsers() {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("code", 200);
		response.put("message", "Lista de usuarios obtenida correctamente");
		response.put("usuarios", usuarioService.findAll());
		LOGGER.info("Usuarios obtenidos: {}", response);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody Usuario usuario) {
		if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario ya existe.");
		}

		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setActive(true);
		usuario.getRole("USER"); // Rol por defecto

		usuarioService.save(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente.");
	}

	@PutMapping("/estado/{id}")
	public ResponseEntity<Map<String, String>> cambiarEstadoUsuario(@PathVariable Integer id,
			@RequestBody Map<String, Boolean> requestBody) {
		LOGGER.info("Solicitud PUT recibida - ID: {}, Nuevo Estado: {}", id, requestBody.get("activo"));

		Optional<Usuario> usuarioOptional = usuarioService.findById(id);
		if (usuarioOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado."));
		}

		Usuario usuario = usuarioOptional.get();
		usuario.setActive(requestBody.get("activo"));
		usuarioService.save(usuario);

		// ✅ Retornamos JSON válido
		return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado correctamente."));
	}

	// Método para obtener un producto por ID
	@GetMapping("getId/{id}")
	public ResponseEntity<Map<String, Object>> getProductoById(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.findById(id);

		if (usuario.isPresent()) {

			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("code", 200);
			response.put("message", "usuario encontrado.");
			response.put("usuario", usuario.get());

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "code", 404, "message", "Usuario no encontrado."));
		}
	}

	@PatchMapping("/rol/{id}")
	public ResponseEntity<?> cambiarRolUsuario(@PathVariable Integer id, @RequestParam String rol) {
		Optional<Usuario> usuarioOptional = usuarioService.findById(id);
		if (usuarioOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
		}

		Usuario usuario = usuarioOptional.get();
		usuario.setRole(rol);
		usuarioService.save(usuario);
		return ResponseEntity.ok("Rol actualizado correctamente.");
	}

	@GetMapping("/compras")
	public ResponseEntity<?> compras(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
		}

		String email = userDetails.getUsername();
		Usuario usuario = usuarioService.findByEmail(email).orElse(null);

		if (usuario == null) {
			LOGGER.warn("Usuario no encontrado para obtener compras.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}

		List<Orden> compras = ordenService.findByUsuario(usuario);
		return ResponseEntity.ok(compras);
	}

	@GetMapping("/detalle/{id}")
	public ResponseEntity<?> detalleCompra(@PathVariable Integer id) {
		LOGGER.info("Consultando detalle de compra, orden ID: {}", id);

		Optional<Orden> ordenOptional = ordenService.findById(id);
		if (ordenOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada");
		}

		return ResponseEntity.ok(ordenOptional.get());
	}
}
