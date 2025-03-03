package com.gestion.automange.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.automange.service.IRegistroVehiculoService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.service.UploadFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.model.Usuario;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "http://localhost:4200/")
public class RegistroVehiculoController {

	private final Logger LOGGER = LoggerFactory.getLogger(RegistroVehiculoController.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IRegistroVehiculoService registroVehiculoService;

	@Autowired
	private UploadFileService upload;

	@GetMapping
	public ResponseEntity<?> getAllRegistroVehiculo() {
		return ResponseEntity.ok(registroVehiculoService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getRegistroVehiculoById(@PathVariable Integer id) {
		Optional<RegistroVehiculo> registroVehiculo = registroVehiculoService.get(id);
		return registroVehiculo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createRegistroVehiculo(@RequestPart("registroVehiculos") String registroVehiculoJson,
			@RequestPart("img") MultipartFile file // Recibe un archivo de imagen
	) throws IOException {

		// Se usa ObjectMapper para convertir el JSON recibido en un objeto de tipo
		// Productos
		ObjectMapper objectMapper = new ObjectMapper();
		RegistroVehiculo registroVehiculo = objectMapper.readValue(registroVehiculoJson, RegistroVehiculo.class);

		// Registrar en logs la información del producto antes de guardarlo
		LOGGER.info("Guardando producto en la DB: {}", registroVehiculo);

		// Se crea un usuario por defecto con ID 1 y se asigna al producto
		Usuario u = new Usuario(1, "", "", "", "", "", "", "");
		registroVehiculo.setUsuario(u);

		// Si el producto es nuevo (no tiene ID), se guarda la imagen
		if (registroVehiculo.getId() == null) {
			// Guardar la imagen en el servidor y obtener el nombre del archivo guardado
			String nombreImagen = upload.saveImages(file, registroVehiculo.getNombre());

			// Asignar el nombre de la imagen al producto
			registroVehiculo.setImagen(nombreImagen);
		}

		// Guardar el producto en la base de datos a través del servicio
		registroVehiculoService.save(registroVehiculo);

		// Retornar la respuesta con código 201 (CREATED) y el producto guardado
		return ResponseEntity.status(HttpStatus.CREATED).body(registroVehiculo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateRegistroVehiculo(@PathVariable Integer id, // Recibe el ID del producto a actualizar
																				// desde la
			// URL
			@RequestPart("productos") String registroVehiculoJson, // Recibe los datos del producto en formato JSON como
																	// String
			@RequestPart(value = "img", required = false) MultipartFile file // La imagen es opcional
	) throws IOException {

		// Convertir el JSON recibido en un objeto de tipo Productos
		ObjectMapper objectMapper = new ObjectMapper();
		RegistroVehiculo registroVehiculo = objectMapper.readValue(registroVehiculoJson, RegistroVehiculo.class);

		LOGGER.info("Actualizando producto en la DB: {}", registroVehiculo);

		// Buscar el producto en la base de datos por ID
		Optional<RegistroVehiculo> optional = registroVehiculoService.get(id);

		if (optional.isPresent()) {
			RegistroVehiculo p = optional.get(); // Obtener el producto existente

			// Verificar si se ha enviado una nueva imagen o si se mantiene la actual
			if (file == null || file.isEmpty()) {
				registroVehiculo.setImagen(p.getImagen()); // Mantener la imagen anterior
			} else {
				// Si el producto tiene una imagen diferente a la predeterminada, eliminar la
				// anterior
				if (p.getImagen() != null && !p.getImagen().equals("default.jpg")) {
					upload.deleteImage(p.getImagen());
				}

				// Guardar la nueva imagen y asignarla al producto
				String nombreImagen = upload.saveImages(file, registroVehiculo.getNombre());
				registroVehiculo.setImagen(nombreImagen);
			}

			// Mantener el usuario asociado al producto anterior
			registroVehiculo.setUsuario(p.getUsuario());

			// Actualizar el producto en la base de datos
			registroVehiculoService.update(registroVehiculo);

			// Retornar la respuesta con el producto actualizado
			return ResponseEntity.ok(registroVehiculo);
		} else {
			// Si no se encuentra el producto, retornar un 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRegistroVehiculo(@PathVariable Integer id) {
		Optional<RegistroVehiculo> optional = registroVehiculoService.get(id);
		if (optional.isPresent()) {
			RegistroVehiculo p = optional.get();
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			registroVehiculoService.delet(id);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
