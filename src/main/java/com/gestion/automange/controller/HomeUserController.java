package com.gestion.automange.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestion.automange.model.DetalleOrden;
import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.service.IDetalleOrdenService;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/") // la raiz del proyecto
public class HomeUserController {

	// instancia del LOGGER para ver datos por consola
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(HomeUserController.class);

	// instancia de objeto - servicio
	@Autowired
	private IProductosService productoService;

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

	// metodo que mapea la vista de usuario en la raiz del proyecto
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		LOGGER.info("sesion usuario: {}", session.getAttribute("idUsuario"));
		model.addAttribute("productos", productoService.findAll());
		model.addAttribute("sesion", session.getAttribute("idUsuario"));
		return "usuario/home";
	}

	// metodo que carga el producto del usuario con el id
	@GetMapping("productoHome/{id}")
	public String productoHome(@PathVariable Integer id, Model model, HttpSession session) {
		LOGGER.info("ID producto enviado como parametro {}", id);
		// variable de clase producto
		Productos p = new Productos();
		// objeto de tipo optional
		Optional<Productos> op = productoService.get(id);
		p = op.get();
		// enviar a la vista con el model los detalles del producto con el id
		model.addAttribute("producto", p);
		model.addAttribute("sesion", session.getAttribute("idUsuario"));
		return "usuario/productoHome";
	}

	// metodo para enviar del boton del producto home al carrito
	@PostMapping("/cart")
	public String addCar(@RequestParam Integer id, @RequestParam Double cantidad, Model model, HttpSession session) {
		DetalleOrden detaorden = new DetalleOrden();
		Productos p = new Productos();
		// variable que siempre que este en el metodo inicialida en cero despues de cada
		// coma
		double sumaTotal = 0;

		Optional<Productos> op = productoService.get(id);
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
		model.addAttribute("sesion", session.getAttribute("idUsuario"));

		return "usuario/carrito";
	}

	// metodo para quitar productos del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model, HttpSession session) {
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

		model.addAttribute("sesion", session.getAttribute("idUsuario"));
		return "usuario/carrito";
	}

	// metodo para redirigir al carrito sun productos
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		// sesion faltante
		model.addAttribute("sesion", session.getAttribute("idUsuario"));
		return "/usuario/carrito";
	}

	// metodo para pasar a la vista del resumen de la orden
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idUsuario").toString())).get();
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", u);
		model.addAttribute("sesion", session.getAttribute("idUsuario"));
		return "usuario/resumenorden";
	}

	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		// guardar orden
		Date fechacreacion = new Date();
		orden.setFechacreacion(fechacreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		// usuario que se refenrencia en esa compra previamente logeado
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idUsuario").toString())).get();
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

	// metodo post para biscar productos en lavista de home usuario
	@PostMapping("/search")
	public String searchProducto(@RequestParam String nombre, Model model) {
		LOGGER.info("nombre del producto: {}", nombre);
		List<Productos> productos = productoService.findAll().stream()
				.filter(p -> p.getNombre().toUpperCase().contains(nombre.toUpperCase())).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

	//

}
