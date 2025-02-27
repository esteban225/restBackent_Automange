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

import com.gestion.automange.service.IRegistroManteService;
import com.gestion.automange.service.IUsuarioService;
import com.gestion.automange.service.UploadFileService;
import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.model.RegistroVehiculo;

@Controller
@RequestMapping("/registroMante")
public class RegistroMantenimientoController {

	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(RegistroMantenimientoController.class);

	@Autowired
	private IRegistroManteService registroManteService;

	@Autowired
	private IUsuarioService usuarioService;

	// micro servicio imgs
	@Autowired
	private UploadFileService upload;
	// metodo para redirigir a la vista show en el template productos

	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("registroMantenimiento", registroManteService.findAll());
		return "registroMante/show";
	}

	// metodo el que redirige a la vista de creacion de productos
	@GetMapping("/create")
	public String create() {
		return "registroMante/create";
	}

	// metodo de creacion de productos
	@PostMapping("/save")
	public String save(RegistroMante registroMante, @RequestParam("img") MultipartFile file) throws IOException {
		LOGGER.info("Este es el objeto del producto a guardar en la DB {}", registroMante);
		RegistroVehiculo u = new RegistroVehiculo(1, null, null, null, null, null, null, null, null);
		registroMante.setRegistroVehiculo(u);
		// validacion imagen de producto
		if (registroMante.getId() == null) {
			String nombreImagen = upload.saveImages(file, registroMante.getNombre());
			registroMante.setImagen(nombreImagen);
		}
		registroManteService.save(registroMante);
		return "redirect:/registroMante";

	}

	// meodo para llemar los imputs de la vista edit
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		RegistroMante p = new RegistroMante();
		Optional<RegistroMante> op = registroManteService.get(id);
		p = op.get();
		LOGGER.info("Busqueda de producto por id {}", p);
		model.addAttribute("mantenimiento", p);
		return "registroMante/edit";
	}

	// metodo para actualizar los datos de un producto
	@PostMapping("/update")
	public String update(RegistroMante registroMante, @RequestParam("img") MultipartFile file) throws IOException {
		LOGGER.info("Este es el objeto del producto a actualizar el DB {}", registroMante);
		RegistroMante p = new RegistroMante();
		p = registroManteService.get(registroMante.getId()).get();
		if (file.isEmpty()) {
			registroMante.setImagen(p.getImagen());
		} else {
			if (!p.getImagen().equals("defaul.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImages(file, p.getNombre());
			registroMante.setImagen(nombreImagen);
		}
		registroMante.setRegistroVehiculo(p.getRegistroVehiculo());
		registroManteService.update(registroMante);
		return "redirect:/registroMante";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		RegistroMante p = new RegistroMante();
		p = registroManteService.get(id).get();
		if (!p.getImagen().equals("defaul.jpg")) {
			upload.deleteImage(p.getImagen());
		}
		registroManteService.delet(id);
		return "redirect:/registroMante";

	}

}
