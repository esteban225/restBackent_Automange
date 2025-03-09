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
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.service.UploadFileService;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

	@Autowired
	private IProductosService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private UploadFileService upload;

	// Método para listar todos los productos
	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllProductos() {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		response.put("code", 200);
		response.put("message", "Lista de productos obtenida correctamente.");
		response.put("productos", productoService.findAll());

		return ResponseEntity.ok(response);
	}

	// Método para obtener un producto por ID
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getProductoById(@PathVariable Integer id) {
		Optional<Productos> producto = productoService.get(id);

		if (producto.isPresent()) {
			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("code", 200);
			response.put("message", "Producto encontrado.");
			response.put("producto", producto.get());

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "code", 404, "message", "Producto no encontrado."));
		}
	}

	// Método para crear un producto
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

		if (productos.getId() == null) {
			String nombreImagen = upload.saveImages(file, productos.getNombre());
			productos.setImagen(nombreImagen);
		}

		productoService.save(productos);

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "success", "code", 201, "message",
				"Producto creado exitosamente.", "producto", productos));
	}

	// Método para actualizar un producto
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateProducto(@PathVariable Integer id,
			@RequestPart("productos") String productosJson,
			@RequestPart(value = "img", required = false) MultipartFile file) throws IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		Productos productos = objectMapper.readValue(productosJson, Productos.class);
		LOGGER.info("Actualizando producto en la DB: {}", productos);

		Optional<Productos> optional = productoService.get(id);

		if (optional.isPresent()) {
			Productos p = optional.get();

			if (file == null || file.isEmpty()) {
				productos.setImagen(p.getImagen());
			} else {
				if (p.getImagen() != null && !p.getImagen().equals("default.jpg")) {
					upload.deleteImage(p.getImagen());
				}
				String nombreImagen = upload.saveImages(file, productos.getNombre());
				productos.setImagen(nombreImagen);
			}

			productos.setUsuario(p.getUsuario());
			productoService.update(productos);

			return ResponseEntity.ok(Map.of("status", "success", "code", 200, "message",
					"Producto actualizado correctamente.", "producto", productos));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					Map.of("status", "error", "code", 404, "message", "No se encontró el producto a actualizar."));
		}
	}

	// Método para eliminar un producto
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Integer id) {
		Optional<Productos> optional = productoService.get(id);

		if (optional.isPresent()) {
			Productos p = optional.get();

			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}

			productoService.delet(id);

			return ResponseEntity
					.ok(Map.of("status", "success", "code", 200, "message", "Producto eliminado correctamente."));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "code", 404, "message", "No se encontró el producto a eliminar."));
		}
	}
}
