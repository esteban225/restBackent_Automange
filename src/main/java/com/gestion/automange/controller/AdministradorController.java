package com.gestion.automange.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.automange.service.IDetalleOrdenService;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.Usuario;

@RestController
@RequestMapping("/api/administrador")
public class AdministradorController {

	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(AdministradorController.class);

	@Autowired
	private IProductosService productosService;
	// inyeccion del servicio de usuario
	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	// Lista de productos
	@GetMapping("/productos")
	public ResponseEntity<?> home() {
		List<Productos> productos = productosService.findAll();
		return ResponseEntity.ok(productos);
	}

	// Lista de usuarios
	@GetMapping("/usuarios")
	public ResponseEntity<?> homeUser() {
		List<Usuario> usuarios = usuarioService.findAll();
		return ResponseEntity.ok(usuarios);
	}

	// Lista de ordenes
	@GetMapping("/ordenes")
	public ResponseEntity<?> homeOrden() {
		List<Orden> ordenes = ordenService.findAll();
		return ResponseEntity.ok(ordenes);
	}

	// Lista de Detalles de orden
	@GetMapping("/detalleOrden")
	public ResponseEntity<?> homeDetalleOrden(@PathVariable Integer id) {
		LOGGER.info("id de la orden: {}");
		Optional<Orden> oreden = ordenService.findById(id);
		return ResponseEntity.ok(oreden.get().getDetalle());
	}
}
