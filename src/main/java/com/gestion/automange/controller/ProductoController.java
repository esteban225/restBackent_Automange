package com.gestion.automange.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

	// metodo para listar todos los poductos
	@GetMapping
	public ResponseEntity<?> getAllProductos() {
		return ResponseEntity.ok(productoService.findAll());
	}

	// metodo para listar por id los poductos
	@GetMapping("/{id}")
	public ResponseEntity<?> getProductoById(@PathVariable Integer id) {
		Optional<Productos> producto = productoService.get(id);
		return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createProducto(@RequestPart("productos") String productosJson, // Recibe un JSON como
																							// String
			@RequestPart("img") MultipartFile file, // Recibe un archivo de imagen
			@AuthenticationPrincipal UserDetails userDetails // Usuario autenticado desde JWT
	) throws IOException {

		if (userDetails == null) {
		    throw new RuntimeException("El usuario no está autenticado");
		}
		// Convertir el JSON recibido en un objeto de tipo Productos
		ObjectMapper objectMapper = new ObjectMapper();
		Productos productos = objectMapper.readValue(productosJson, Productos.class);

		// Registrar en logs la información del producto antes de guardarlo
		LOGGER.info("Guardando producto en la DB: {}", productos);

		// Obtener el usuario autenticado desde UserDetails (proveniente del token JWT)
		Usuario usuarioAutenticado = usuarioService.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		productos.setUsuario(usuarioAutenticado);

		// Si el producto es nuevo (no tiene ID), procesar y guardar la imagen
		if (productos.getId() == null) {
			// Guardar la imagen en el servidor y obtener el nombre del archivo guardado
			String nombreImagen = upload.saveImages(file, productos.getNombre());

			// Asignar el nombre de la imagen al producto
			productos.setImagen(nombreImagen);
		}

		// Guardar el producto en la base de datos a través del servicio
		productoService.save(productos);

		// Retornar la respuesta con código 201 (CREATED) y el producto guardado
		return ResponseEntity.status(HttpStatus.CREATED).body(productos);
	}

	// metodo para actualizar los poductos
	@PutMapping("/{id}")
	public ResponseEntity<?> updateProducto(@PathVariable Integer id, // Recibe el ID del producto a actualizar desde la
																		// URL
			@RequestPart("productos") String productosJson, // Recibe los datos del producto en formato JSON como String
			@RequestPart(value = "img", required = false) MultipartFile file // La imagen es opcional
	) throws IOException {

		// Convertir el JSON recibido en un objeto de tipo Productos
		ObjectMapper objectMapper = new ObjectMapper();
		Productos productos = objectMapper.readValue(productosJson, Productos.class);

		LOGGER.info("Actualizando producto en la DB: {}", productos);

		// Buscar el producto en la base de datos por ID
		Optional<Productos> optional = productoService.get(id);

		if (optional.isPresent()) {
			Productos p = optional.get(); // Obtener el producto existente

			// Verificar si se ha enviado una nueva imagen o si se mantiene la actual
			if (file == null || file.isEmpty()) {
				productos.setImagen(p.getImagen()); // Mantener la imagen anterior
			} else {
				// Si el producto tiene una imagen diferente a la predeterminada, eliminar la
				// anterior
				if (p.getImagen() != null && !p.getImagen().equals("default.jpg")) {
					upload.deleteImage(p.getImagen());
				}

				// Guardar la nueva imagen y asignarla al producto
				String nombreImagen = upload.saveImages(file, productos.getNombre());
				productos.setImagen(nombreImagen);
			}

			// Mantener el usuario asociado al producto anterior
			productos.setUsuario(p.getUsuario());

			// Actualizar el producto en la base de datos
			productoService.update(productos);

			// Retornar la respuesta con el producto actualizado
			return ResponseEntity.ok(productos);
		} else {
			// Si no se encuentra el producto, retornar un 404 Not Found
			return ResponseEntity.notFound().build();
		}
	}

	// metodo para eliminar los poductos
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProducto(@PathVariable Integer id) {
		Optional<Productos> optional = productoService.get(id);
		if (optional.isPresent()) {
			Productos p = optional.get();
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			productoService.delet(id);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
