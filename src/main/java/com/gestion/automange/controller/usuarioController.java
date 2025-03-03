package com.gestion.automange.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.automange.service.IDetalleOrdenService;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.model.DetalleOrden;
import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.Usuario;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200/")
public class usuarioController {

	// instancia del LOGGER para ver datos por consola
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(usuarioController.class);

	@Autowired
	private IProductosService productosService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	// dos variables
	// lista de detalles de la orden para guardarlos en la db
	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	// objeto que almacena los datos de la orden
	Orden orden = new Orden();

	@GetMapping
	public ResponseEntity<?> tiendaEcommerce() {
		return ResponseEntity.ok(productosService.findAll());
	}

	// metodo que carga el producto del usuario con el id
	@GetMapping("/{id}")
	public ResponseEntity<?> productoHome(@PathVariable Integer id) {
		LOGGER.info("ID producto enviado como parametro {}", id);
		Optional<Productos> producto = productosService.get(id);
		return producto.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
	}

	// metodo para enviar del boton del producto home al carrito
	@PostMapping("/cart")
	public String addCar(@RequestParam Integer id, @RequestParam Double cantidad, Model model) {
		DetalleOrden detaorden = new DetalleOrden();
		Productos p = new Productos();
		// variable que siempre que este en el metodo inicialida en cero despues de cada
		// coma
		double sumaTotal = 0;

		Optional<Productos> op = productosService.get(id);
		LOGGER.info("Producto añadido: {}", op.get());
		LOGGER.info("Cantidad añadida: {}", cantidad);

		p = op.get();
		detaorden.setCantidad(cantidad);
		detaorden.setPrecio(p.getPrecio());
		detaorden.setNombre(p.getNombre());
		detaorden.setTotal(p.getPrecio() * cantidad);
		detaorden.setProducto(p);
		// validacion para evitar duplicados de productos
		Integer idProducto = p.getId();
		// funciON LAMDA stram y funcion anonima con predicado anyMatch
		boolean insertado = detalles.stream().anyMatch(prod -> prod.getProducto().getId() == idProducto);
		// si no es true añade el producto
		if (!insertado) {
			// detalles
			detalles.add(detaorden);
		}

		// suma de totales de la lista que el usuario añada al carrito
		// funcion de lava 8 lamda stream
		// funcion java 8 anonima dt
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		// pasar variables a la vista
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "tiendaEcommerce/carrito";
	}

	// metodo para quitar productos del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		// lista nueva de productos
		List<DetalleOrden> ordenesNuevas = new ArrayList<DetalleOrden>();
		// quitar objeto de la lista de detalleOrden

		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNuevas.add(detalleOrden);
			}
		}
		// poner la nueva lista con los productos restantes del carrito
		detalles = ordenesNuevas;
		// recalcular los productos del carrito
		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		// pasar variables a la vista
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "tiendaEcommerce/carrito";
	}

	// metodo para redirigir al carrito sun productos
	@GetMapping("/getCart")
	public String getCart(Model model) {
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "/tiendaEcommerce/carrito";
	}

	// metodo para pasar a la vista del resumen de la orden
	@GetMapping("/order")
	public String order(Model model) {
		Usuario u = usuarioService.findById(1).get();
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", u);
		return "tiendaEcommerce/resumenorden";
	}

	@GetMapping("/saveOrder")
	public String saveOrder() {
		// guardar orden
		Date fechacreacion = new Date();
		orden.setFechacreacion(fechacreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		// usuario que se refenrencia en esa compra previamente logeado
		Usuario u = usuarioService.findById(1).get();
		orden.setUsuario(u);
		ordenService.save(orden);
		// guardar detalles de la orden
		for (DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		// limpiar valores que no se anaden a la orden recien guardada
		orden = new Orden();
		detalles.clear();
		return "redirect:/";
	}

}
