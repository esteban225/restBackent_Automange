package com.gestion.automange.controller;

import java.io.IOException;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;

import com.gestion.automange.service.IRegistroVehiculoService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.service.UploadFileService;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.model.Usuario;

@Controller
@RequestMapping("/registroVehiculos")
public class RegistroVehiculoController {

	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(RegistroVehiculoController.class);

	@Autowired
	private IRegistroVehiculoService registroVehiculoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private UploadFileService upload;

	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("registroVehiculo", registroVehiculoService.findAll());
		return "registroVehiculos/show";
	}

	@GetMapping("/create")
	public String create() {
		return "registroVehiculos/create";
	}

	@PostMapping("/save")
	public String save(RegistroVehiculo registroVehiculo, @RequestParam("img") MultipartFile file) throws IOException {
		LOGGER.info("Este es el objeto del producto a guardar en la DB {}", registroVehiculo);
		Usuario u = new Usuario(1, "", "", "", "", "", "", "");
		registroVehiculo.setUsuario(u);

		if (registroVehiculo.getId() == null) {
			String nombreImagen = upload.saveImages(file, registroVehiculo.getNombre());
			registroVehiculo.setImagen(nombreImagen);
		}
		registroVehiculoService.save(registroVehiculo);
		return "redirect:/registroVehiculos";

	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		RegistroVehiculo p = new RegistroVehiculo();
		Optional<RegistroVehiculo> op = registroVehiculoService.get(id);
		p = op.get();
		LOGGER.info("Busqueda de producto por id {}", p);
		model.addAttribute("vehiculo", p);
		return "registroVehiculos/edit";
	}

	@PostMapping("/update")
	public String update(RegistroVehiculo registroVehiculos, @RequestParam("img") MultipartFile file)
			throws IOException {
		LOGGER.info("Este es el objeto del producto a actualizar el DB {}", registroVehiculos);
		RegistroVehiculo p = new RegistroVehiculo();
		p = registroVehiculoService.get(registroVehiculos.getId()).get();
		if (file.isEmpty()) {
			registroVehiculos.setImagen(p.getImagen());
		} else {
			if (!p.getImagen().equals("defaul.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImages(file, p.getNombre());
			registroVehiculoService.setImagen(nombreImagen);
		}
		registroVehiculos.setUsuario(p.getUsuario());
		registroVehiculoService.update(registroVehiculos);
		return "redirect:/registroVehiculos";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		RegistroVehiculo p = new RegistroVehiculo();
		p = registroVehiculoService.get(id).get();
		if (!p.getImagen().equals("defaul.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		registroVehiculoService.delet(id);
		return "redirect:/registroVehiculos";

	}
}
