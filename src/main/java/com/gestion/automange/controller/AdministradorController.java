package com.gestion.automange.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.automange.service.ICitasService;
import com.gestion.automange.service.IDetalleOrdenService;
import com.gestion.automange.service.IOrdenService;
import com.gestion.automange.service.IProductosService;
import com.gestion.automange.service.IRegistroManteService;
import com.gestion.automange.service.IRegistroVehiculoService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.model.Citas;
import com.gestion.automange.model.DetalleOrden;
import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Productos;
import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.model.Usuario;

@RestController
@RequestMapping("/api/administrador")
public class AdministradorController {
	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(AdministradorController.class);

	@Autowired
	private IProductosService productosService;

	@Autowired
	private IRegistroVehiculoService vehiculoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;


	// metodo para listar productos
	@GetMapping
	public ResponseEntity<?> homeProductos() {
		List<Productos> productos = productosService.findAll();
		return ResponseEntity.ok(productos);
	}

	// metodo para listar los usuarios
	@GetMapping("/usuarios")
	public ResponseEntity<?> getUsuarios() {
		List<Usuario> usuarios = usuarioService.findAll();
		return ResponseEntity.ok(usuarios);
	}

	// metodo para listar vehiculos
	@GetMapping("/vehiculos")
	public ResponseEntity<?> getVehiculos() {
		List<RegistroVehiculo> vehiculos = vehiculoService.findAll();
		return ResponseEntity.ok(vehiculos);
	}

	// metodo para listar los mantenimientos del vehiculo
	@GetMapping("/vehiculoMantenimiento/{id}")
	public ResponseEntity<?> getvehiculoMantenimiento(@PathVariable Integer id) {
		LOGGER.info("Id del vehiculo: {} ", id);
		Optional<RegistroVehiculo> vehiculo = vehiculoService.findById(id);
		if (!vehiculo.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("vehiculo no existe en la base de datos");
		}
		List<RegistroMante> manteniminento = vehiculo.get().getRegistroMante();

		return ResponseEntity.ok(manteniminento);
	}

	// metodo para listar las ordenes
	@GetMapping("/ordenes")
	public ResponseEntity<?> getOrdenes() {
		List<Orden> ordenes = ordenService.findAll();
		return ResponseEntity.ok(ordenes);
	}

//metodo para lsitar el detalle de las ordenes
	@GetMapping("/detallesOrdenes/{id}")
	public ResponseEntity<?> getOrdenesDetalles(@PathVariable Integer id) {
		LOGGER.info("Id de la orden: {} ", id);
		Optional<Orden> orden = ordenService.findById(id);
		if (!orden.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no Encontrada");
		}

		List<DetalleOrden> detalles = orden.get().getDetalle();
		return ResponseEntity.ok(detalles);
	}

	// metodo para listar las citas del usuario
	@GetMapping("/usuarioCita/{id}")
	public ResponseEntity<?> getcitasMantenimiento(@PathVariable Integer id) {
		LOGGER.info("Id del usuario: {} ", id);
		Optional<Usuario> usuario = usuarioService.findById(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("usuario no existe en la base de datos");
		}
		List<Citas> cita = usuario.get().getCitas();

		return ResponseEntity.ok(cita);
	}

	// añadir lista de vehiculos, usuarios, ordenes, detalles de
	// orden,citas de matenimientos, mantenimientos
}
