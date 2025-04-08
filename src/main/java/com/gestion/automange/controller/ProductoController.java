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
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.CloudinaryService;
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IUsuarioService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

	@Autowired
	private IProductosService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private CloudinaryService cloudinaryService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllProductos() {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("code", 200);
		response.put("message", "Lista de productos obtenida correctamente.");
		response.put("productos", productoService.findAll());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getProductoById(@PathVariable Integer id) {
		Optional<Productos> producto = productoService.get(id);
		if (producto.isPresent()) {
			return ResponseEntity.ok(Map.of("status", "success", "code", 200, "message", "Producto encontrado.",
					"producto", producto.get()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "code", 404, "message", "Producto no encontrado."));
		}
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> createProducto(@RequestPart("productos") String productosJson,
			@RequestPart("img") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails)
			throws IOException {

		if (userDetails == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("status", "error", "code", 401, "message", "El usuario no está autenticado."));
		}

		ObjectMapper objectMapper = new ObjectMapper();
		Productos productos = objectMapper.readValue(productosJson, Productos.class);
		LOGGER.info("Guardando producto en la DB: {}", productos);

		Usuario usuarioAutenticado = usuarioService.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

		productos.setUsuario(usuarioAutenticado);

		if (productos.getId() == null && file != null && !file.isEmpty()) {
			String imageUrl = cloudinaryService.uploadImage(file, productos.getNombre(), "productos");
			productos.setImagen(imageUrl);
		}

		productoService.save(productos);

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "success", "code", 201, "message",
				"Producto creado exitosamente.", "producto", productos));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateProducto(@PathVariable Integer id,
			@RequestPart("productos") String productosJson,
			@RequestPart(value = "img", required = false) MultipartFile file) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		Productos productos = objectMapper.readValue(productosJson, Productos.class);
		LOGGER.info("Actualizando producto en la DB: {}", productos);

		Optional<Productos> optional = productoService.get(id);
		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					Map.of("status", "error", "code", 404, "message", "No se encontró el producto a actualizar."));
		}

		Productos productoExistente = optional.get();

		if (file != null && !file.isEmpty()) {
			if (productoExistente.getImagen() != null && !productoExistente.getImagen().isEmpty()) {
				String publicId = getPublicIdFromUrl(productoExistente.getImagen());
				cloudinaryService.deleteImage(publicId);
			}
			String newImageUrl = cloudinaryService.uploadImage(file, productos.getNombre(), "productos");
			productos.setImagen(newImageUrl);
		} else {
			productos.setImagen(productoExistente.getImagen());
		}

		productos.setUsuario(productoExistente.getUsuario());
		productoService.update(productos);

		return ResponseEntity.ok(Map.of("status", "success", "code", 200, "message",
				"Producto actualizado correctamente.", "producto", productos));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Integer id) {
		Optional<Productos> optional = productoService.get(id);
		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "code", 404, "message", "No se encontró el producto a eliminar."));
		}

		Productos producto = optional.get();

		if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
			String publicId = getPublicIdFromUrl(producto.getImagen());
			try {
				cloudinaryService.deleteImage(publicId);
			} catch (IOException e) {
				LOGGER.error("Error al eliminar imagen de Cloudinary: {}", e.getMessage());
			}
		}

		productoService.delet(id);

		return ResponseEntity
				.ok(Map.of("status", "success", "code", 200, "message", "Producto eliminado correctamente."));
	}

	private String getPublicIdFromUrl(String imageUrl) {
		try {
			String[] parts = imageUrl.split("/");
			String filename = parts[parts.length - 1];
			String folder = parts[parts.length - 2];
			return folder + "/" + filename.split("\\.")[0];
		} catch (Exception e) {
			LOGGER.error("No se pudo obtener el publicId de la URL: {}", imageUrl);
			return null;
		}
	}
}
