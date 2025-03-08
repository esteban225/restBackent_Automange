package com.gestion.automange.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.automange.model.Citas;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.ICitasService;

@RestController
@RequestMapping("/api/cita")
@CrossOrigin(origins = "http://localhost:4200/")
public class UsuarioCitaController {

	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(UsuarioCitaController.class);

	@Autowired
	private ICitasService citasService;

	@GetMapping("/citaAdmin")
	public ResponseEntity<?> citasAdmin() {
		List<Citas> Citas = citasService.findAll();
		return ResponseEntity.ok(Citas);
	}

	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody Citas citas) {
		LOGGER.info("Información de la cita: {}", citas);

		// Asignar un usuario por defecto (debería obtenerse de la base de datos en un
		// caso real)
		Usuario usuario = new Usuario(1, null, null, null, null, null, null, null);
		citas.setUsuario(usuario);

		// Guardar la cita (se asume que hay un servicio para manejar esto)
		Citas nuevaCita = citasService.save(citas); // Debes tener un método save en tu servicio

		return ResponseEntity.ok(nuevaCita);
	}

}
