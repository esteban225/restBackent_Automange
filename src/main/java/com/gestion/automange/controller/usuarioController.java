package com.gestion.automange.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importa las clases necesarias para manejar HTTP
import org.springframework.http.ResponseEntity; // Importa las clases necesarias para manejar HTTP
import org.springframework.web.bind.annotation.*;

import com.gestion.automange.service.IDetalleOrdenService;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.model.DetalleOrden;
import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.Usuario;

@RestController // Cambia a @RestController para definir un controlador REST
@RequestMapping("/api") // Define un prefijo de ruta para todas las solicitudes
public class usuarioController {

	private final Logger LOGGER = LoggerFactory.getLogger(usuarioController.class);

	@Autowired
	private IProductosService productosService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	private List<DetalleOrden> detalles = new ArrayList<>();
	private Orden orden = new Orden();

	@GetMapping("/tienda")
	public ResponseEntity<List<Productos>> tiendaEcommerce() {
		List<Productos> productos = productosService.findAll();
		return ResponseEntity.ok(productos);
	}

	@GetMapping("/productoHome/{id}")
	public ResponseEntity<?> productoHome(@PathVariable Integer id) {
		LOGGER.info("ID producto enviado como parametro {}", id);
		Optional<Productos> op = productosService.get(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
		}
		return ResponseEntity.ok(op.get());
	}

	@PostMapping("/cart")
	public ResponseEntity<?> addCar(@RequestParam Integer id, @RequestParam Double cantidad) {
		DetalleOrden detaorden = new DetalleOrden();
		double sumaTotal = 0;

		Optional<Productos> op = productosService.get(id);
		if (!op.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
		}

		Productos p = op.get();
		LOGGER.info("Producto añadido: {}", p);
		LOGGER.info("Cantidad añadida: {}", cantidad);

		detaorden.setCantidad(cantidad);
		detaorden.setPrecio(p.getPrecio());
		detaorden.setNombre(p.getNombre());
		detaorden.setTotal(p.getPrecio() * cantidad);
		detaorden.setProducto(p);

		Integer idProducto = p.getId();
		boolean insertado = detalles.stream().anyMatch(prod -> prod.getProducto().getId().equals(idProducto));
		if (!insertado) {
			detalles.add(detaorden);
		}

		sumaTotal = detalles.stream().mapToDouble(DetalleOrden::getTotal).sum();
		orden.setTotal(sumaTotal);

		return ResponseEntity.ok(detalles);
	}

	@GetMapping("/delete/cart/{id}")
	public ResponseEntity<?> deleteProductoCart(@PathVariable Integer id) {
		List<DetalleOrden> ordenesNuevas = new ArrayList<>();

		for (DetalleOrden detalleOrden : detalles) {
			if (!detalleOrden.getProducto().getId().equals(id)) {
				ordenesNuevas.add(detalleOrden);
			}
		}

		detalles = ordenesNuevas;
		double sumaTotal = detalles.stream().mapToDouble(DetalleOrden::getTotal).sum();
		orden.setTotal(sumaTotal);

		return ResponseEntity.ok(detalles);
	}

	@GetMapping("/getCart")
	public ResponseEntity<?> getCart() {
		return ResponseEntity.ok(detalles);
	}

	@GetMapping("/order")
	public ResponseEntity<?> order() {
		Usuario u = usuarioService.findById(1).get();
		return ResponseEntity.ok(new Object[] { detalles, orden, u });
	}

	@PostMapping("/saveOrder")
	public ResponseEntity<String> saveOrder() {
		Date fechacreacion = new Date();
		orden.setFechacreacion(fechacreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		Usuario u = usuarioService.findById(1).get();
		orden.setUsuario(u);
		ordenService.save(orden);

		for (DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}

		orden = new Orden();
		detalles.clear();
		return ResponseEntity.ok("Orden guardada exitosamente");
	}
}
