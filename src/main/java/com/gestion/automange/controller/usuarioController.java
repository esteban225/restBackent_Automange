package com.gestion.automange.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/usuariosHome")
@CrossOrigin(origins = "http://localhost:4200/")
public class usuarioController {

	// instancia del LOGGER para ver datos por consola
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(usuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	// Nuevo objeto encryptador
	BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();

	@PostMapping("/registro")
	public Usuario createUser(@RequestBody Usuario usuario) {
		usuario.setTipo("USER");
		usuario.setPassword(passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		return usuario;
	}

	@PostMapping("/save")
	public Usuario save(@RequestBody Usuario usuario) {
		LOGGER.info("Usuario a registrar: {}", usuario);
		usuario.setTipo("USER");
		usuario.setPassword(passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		return usuario;
	}

	@PostMapping("/login")
	public String login(@RequestBody Usuario usuario, HttpSession session) {
		LOGGER.info("Accesos: {}", usuario);
		Optional<Usuario> user = usuarioService
				.findById(Integer.parseInt(session.getAttribute("idUsuario").toString()));
		if (user.isPresent()) {
			session.setAttribute("idUsuario", user.get().getId());
			return "Login successful";
		} else {
			LOGGER.warn("Usuario no existe en db");
			return "User not found";
		}
	}

	@GetMapping("/acceder")
	public String acceder(@RequestBody Usuario usuario, HttpSession session) {
		LOGGER.info("Accesos: {}", usuario);
		Optional<Usuario> user = usuarioService
				.findById(Integer.parseInt(session.getAttribute("idUsuario").toString()));
		LOGGER.info("Usuario db obtenido: {}", user.get());
		if (user.isPresent()) {
			session.setAttribute("idUsuario", user.get().getId());
			if (user.get().getTipo().equals("ADMIN")) {
				return "Redirect to /administrador";
			} else {
				return "Redirect to /";
			}
		} else {
			LOGGER.warn("Usuario no existe en db");
			return "User not found";
		}
	}

	@GetMapping("/cerrar")
	public String cerrarSesion(HttpSession session) {
		session.removeAttribute("idUsuario");
		return "Session closed";
	}

	@GetMapping("/compras")
	public List<Orden> compras(HttpSession session) {
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idUsuario").toString())).get();
		return ordenService.findByUsuario(usuario);
	}

	@GetMapping("/detalle/{id}")
	public Orden detalleCompra(HttpSession session, @PathVariable Integer id) {
		LOGGER.info("Id orden: {}", id);
		return ordenService.findById(id).orElse(null);
	}

}
