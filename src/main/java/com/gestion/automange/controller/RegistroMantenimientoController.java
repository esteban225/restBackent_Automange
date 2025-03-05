package com.gestion.automange.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.automange.service.IRegistroManteService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.service.UploadFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.model.Usuario;

@RestController
@RequestMapping("/api/registroMante")
@CrossOrigin(origins = "http://localhost:4200/")
public class RegistroMantenimientoController {

	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(RegistroMantenimientoController.class);

	@Autowired
	private IRegistroManteService registroManteService;

	@Autowired
	private IUsuarioService usuarioService;

	// micro servicio imgs
	@Autowired
	private UploadFileService upload;
	// metodo para redirigir a la vista show en el template productos

	@GetMapping
	public ResponseEntity<?> getAllRegistroMante() {
		return ResponseEntity.ok(registroManteService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getRegistroManteById(@PathVariable Integer id) {
		Optional<RegistroMante> registroMante = registroManteService.get(id);
		return registroMante.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createRegistroMante(@RequestPart("registroMante") String registroManteJson, // Recibe un JSON como
																							// String
			@RequestPart("img") MultipartFile file // Recibe un archivo de imagen
	) throws IOException {

		// Se usa ObjectMapper para convertir el JSON recibido en un objeto de tipo
		// Productos
		ObjectMapper objectMapper = new ObjectMapper();
		RegistroMante registroMante = objectMapper.readValue(registroManteJson, RegistroMante.class);

		// Registrar en logs la información del producto antes de guardarlo
		LOGGER.info("Guardando producto en la DB: {}", registroMante);

		// Se crea un usuario por defecto con ID 1 y se asigna al producto
		RegistroVehiculo r = new RegistroVehiculo(1, registroManteJson, registroManteJson, null, null, registroManteJson, registroManteJson, null, null);
		registroMante.setRegistroVehiculo(r);

		// Si el producto es nuevo (no tiene ID), se guarda la imagen
		if (registroMante.getId() == null) {
			// Guardar la imagen en el servidor y obtener el nombre del archivo guardado
			String nombreImagen = upload.saveImages(file, registroMante.getNombre());

			// Asignar el nombre de la imagen al producto
			registroMante.setImagen(nombreImagen);
		}

		// Guardar el producto en la base de datos a través del servicio
		registroManteService.save(registroMante);

		// Retornar la respuesta con código 201 (CREATED) y el producto guardado
		return ResponseEntity.status(HttpStatus.CREATED).body(registroMante);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateRegistroMante(@PathVariable Integer id, // Recibe el ID del producto a actualizar desde la
																		// URL
			@RequestPart("productos") String registroManteJson, // Recibe los datos del producto en formato JSON como String
			@RequestPart(value = "img", required = false) MultipartFile file // La imagen es opcional
	) throws IOException {

		// Convertir el JSON recibido en un objeto de tipo Productos
		ObjectMapper objectMapper = new ObjectMapper();
		RegistroMante registroMante = objectMapper.readValue(registroManteJson, RegistroMante.class);

		LOGGER.info("Actualizando producto en la DB: {}", registroMante);

		// Buscar el producto en la base de datos por ID
		Optional<RegistroMante> optional = registroManteService.get(id);

		if (optional.isPresent()) {
			RegistroMante p = optional.get(); // Obtener el producto existente

			// Verificar si se ha enviado una nueva imagen o si se mantiene la actual
			if (file == null || file.isEmpty()) {
				registroMante.setImagen(p.getImagen()); // Mantener la imagen anterior
			} else {
				// Si el producto tiene una imagen diferente a la predeterminada, eliminar la
				// anterior
				if (p.getImagen() != null && !p.getImagen().equals("default.jpg")) {
					upload.deleteImage(p.getImagen());
				}

				// Guardar la nueva imagen y asignarla al producto
				String nombreImagen = upload.saveImages(file, registroMante.getNombre());
				registroMante.setImagen(nombreImagen);
			}

			// Mantener el usuario asociado al producto anterior
			registroMante.setRegistroVehiculo(p.getRegistroVehiculo());

			// Actualizar el producto en la base de datos
			registroManteService.update(registroMante);

			// Retornar la respuesta con el producto actualizado
			return ResponseEntity.ok(registroMante);
		} else {
			// Si no se encuentra el producto, retornar un 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRegistroMante(@PathVariable Integer id) {
		Optional<RegistroMante> optional = registroManteService.get(id);
		if (optional.isPresent()) {
			RegistroMante p = optional.get();
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			registroManteService.delet(id);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
