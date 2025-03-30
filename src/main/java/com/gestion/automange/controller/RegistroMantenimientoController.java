package com.gestion.automange.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.automange.dto.RegistroManteDTO;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.service.IRegistroManteService;
import com.gestion.automange.service.IRegistroVehiculoService;
import com.gestion.automange.service.UploadFileService;

@RestController
@RequestMapping("/api/registroMante")
@CrossOrigin(origins = "http://localhost:4200/")
public class RegistroMantenimientoController {

	private final Logger LOGGER = LoggerFactory.getLogger(RegistroMantenimientoController.class);
	private final String BASE_IMAGE_URL = "http://localhost:13880/images/";

	@Autowired
	private IRegistroManteService registroManteService;

	@Autowired
	private IRegistroVehiculoService registroVehiculoService;

	@Autowired
	private UploadFileService upload;

	@GetMapping
	public ResponseEntity<?> getAllRegistroMante() {
		return ResponseEntity.ok(registroManteService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getRegistroManteById(@PathVariable Integer id) {
		Optional<RegistroMante> registroMante = registroManteService.get(id);
		return registroMante.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createRegistroMante(@RequestPart("mantenimiento") String registroManteJson,
			@RequestPart("img") MultipartFile file) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			RegistroManteDTO registroManteDTO = objectMapper.readValue(registroManteJson, RegistroManteDTO.class);

			// Validar campos obligatorios
			if (registroManteDTO.getNombre() == null || file.isEmpty()) {
				return ResponseEntity.badRequest().body("Nombre e imagen son obligatorios");
			}

			Optional<RegistroVehiculo> vehiculoOpt = registroVehiculoService.get(registroManteDTO.getVehiculoId());
			if (!vehiculoOpt.isPresent()) {
				return ResponseEntity.badRequest().body("Vehículo no encontrado");
			}

			RegistroMante registroMante = new RegistroMante();
			registroMante.setFechaMante(registroManteDTO.getFechaMante());
			registroMante.setNombre(registroManteDTO.getNombre());
			registroMante.setCaracteristrica(registroManteDTO.getCaracteristica()); // Corregir posible typo
			registroMante.setPrecio(registroManteDTO.getPrecio());
			registroMante.setRegistroVehiculo(vehiculoOpt.get());

			String nombreImagen = upload.saveImages(file, registroMante.getNombre());
			registroMante.setImagen(BASE_IMAGE_URL + nombreImagen);

			registroMante = registroManteService.save(registroMante);
			return ResponseEntity.status(HttpStatus.CREATED).body(registroMante);

		} catch (JsonProcessingException e) {
			return ResponseEntity.badRequest().body("Formato JSON inválido");
		}
	}

	// Método para actualizar un producto
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateRegistroMante(@PathVariable Integer id,
			@RequestPart("mantenimiento") String mantenimientoJson,
			@RequestPart(value = "img", required = false) MultipartFile file) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			RegistroManteDTO dto = objectMapper.readValue(mantenimientoJson, RegistroManteDTO.class);
			Optional<RegistroMante> optional = registroManteService.get(id);

			if (!optional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("status", "error", "message", "Registro no encontrado"));
			}

			RegistroMante existing = optional.get();

			// Actualizar campos permitidos desde el DTO
			existing.setFechaMante(dto.getFechaMante());
			existing.setNombre(dto.getNombre());
			existing.setCaracteristrica(dto.getCaracteristica());
			existing.setPrecio(dto.getPrecio());

			// Validar y actualizar vehículo si es necesario
			if (dto.getVehiculoId() != null) {
				Optional<RegistroVehiculo> vehiculoOpt = registroVehiculoService.get(dto.getVehiculoId());
				if (!vehiculoOpt.isPresent()) {
					return ResponseEntity.badRequest().body("Vehículo no encontrado");
				}
				existing.setRegistroVehiculo(vehiculoOpt.get());
			}

			// Manejo de imagen
			if (file != null && !file.isEmpty()) {
				if (existing.getImagen() != null && !existing.getImagen().equals("default.jpg")) {
					upload.deleteImage(existing.getImagen());
				}
				String nombreImagen = upload.saveImages(file, existing.getNombre());
				existing.setImagen(BASE_IMAGE_URL + nombreImagen);
			}

			registroManteService.save(existing);
			return ResponseEntity.ok(Map.of("status", "success", "data", existing));

		} catch (JsonProcessingException e) {
			return ResponseEntity.badRequest().body("JSON inválido");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRegistroMante(@PathVariable Integer id) {
		Optional<RegistroMante> optional = registroManteService.get(id);
		if (!optional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		RegistroMante registroMante = optional.get();
		if (!registroMante.getImagen().equals("default.jpg")) {
			upload.deleteImage(registroMante.getImagen());
		}
		registroManteService.delet(id);
		return ResponseEntity.noContent().build();
	}
}
