package com.gestion.automange.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.CloudinaryService;
import com.gestion.automange.service.IRegistroVehiculoService;
import com.gestion.automange.service.IUsuarioService;


// holaaaaaa
@RestController
@RequestMapping("/api/vehiculos")
public class RegistroVehiculoController {

	private final Logger LOGGER = LoggerFactory.getLogger(RegistroVehiculoController.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IRegistroVehiculoService registroVehiculoService;

	@Autowired
	private CloudinaryService cloudinaryService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllRegistroVehiculo() {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("code", 200);
		response.put("message", "Lista de vehiculos obtenida correctamente.");
		response.put("vehiculos", registroVehiculoService.findAll());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getProductoById(@PathVariable Integer id) {
		Optional<RegistroVehiculo> vehiculo = registroVehiculoService.get(id);

		if (vehiculo.isPresent()) {
			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("code", 200);
			response.put("message", "vehiculo encontrado.");
			response.put("vehiculo", vehiculo.get());
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "code", 404, "message", "Vehículo no encontrado."));
		}
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createRegistroVehiculo(@RequestPart("vehiculo") String registroVehiculoJson,
			@RequestPart("img") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails)
			throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		RegistroVehiculo registroVehiculo = objectMapper.readValue(registroVehiculoJson, RegistroVehiculo.class);

		LOGGER.info("Guardando vehículo en la DB: {}", registroVehiculo);

		Usuario usuarioAutenticado = usuarioService.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		registroVehiculo.setUsuario(usuarioAutenticado);

		if (registroVehiculo.getId() == null && file != null && !file.isEmpty()) {
			String imageUrl = cloudinaryService.uploadImage(file, registroVehiculo.getNombre(), "vehiculos");
			registroVehiculo.setImagen(imageUrl);
		}

		registroVehiculoService.save(registroVehiculo);
		return ResponseEntity.status(HttpStatus.CREATED).body(registroVehiculo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateRegistroVehiculo(@PathVariable Integer id,
			@RequestPart("vehiculo") String registroVehiculoJson,
			@RequestPart(value = "img", required = false) MultipartFile file) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		RegistroVehiculo registroVehiculo = objectMapper.readValue(registroVehiculoJson, RegistroVehiculo.class);

		LOGGER.info("Actualizando vehículo en la DB: {}", registroVehiculo);

		Optional<RegistroVehiculo> optional = registroVehiculoService.get(id);

		if (optional.isPresent()) {
			RegistroVehiculo existente = optional.get();

			if (file == null || file.isEmpty()) {
				registroVehiculo.setImagen(existente.getImagen());
			} else {
				if (existente.getImagen() != null && !existente.getImagen().contains("default.jpg")) {
					cloudinaryService.deleteImage(existente.getImagen());
				}
				String imageUrl = cloudinaryService.uploadImage(file, registroVehiculo.getNombre(), "vehiculos");
				registroVehiculo.setImagen(imageUrl);
			}

			registroVehiculo.setUsuario(existente.getUsuario());
			registroVehiculoService.update(registroVehiculo);
			return ResponseEntity.ok(registroVehiculo);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRegistroVehiculo(@PathVariable Integer id) {
		Optional<RegistroVehiculo> optional = registroVehiculoService.get(id);

		if (optional.isPresent()) {
			RegistroVehiculo vehiculo = optional.get();
			if (vehiculo.getImagen() != null && !vehiculo.getImagen().contains("default.jpg")) {
				try {
					cloudinaryService.deleteImage(vehiculo.getImagen());
				} catch (IOException e) {
					LOGGER.error("Error eliminando imagen de Cloudinary: {}", e.getMessage());
				}
			}
			registroVehiculoService.delet(id);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
