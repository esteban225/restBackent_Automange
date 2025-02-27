package com.gestion.automange.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gestion.automange.service.ICitasService;
import com.gestion.automange.model.Citas;
import com.gestion.automange.model.Usuario;
@Controller
@RequestMapping("/cita")
public class UsuarioCitaController {

	private final Logger LOGGER = (Logger) LoggerFactory.getLogger(UsuarioCitaController.class);
	
	@Autowired
	private ICitasService citasService;
	
	@GetMapping("")
	public String homeCitas() {
		return"usuario/usuario_cita";
	}
	
	@GetMapping("/citaAdmin")
	public String citasAdmin(Model model) {
		model.addAttribute("citas", citasService.findAll());
		return "citasAdmin/show";
	}
	
	@PostMapping("/save")
	public String save(Citas citas) {
		LOGGER.info("informacion cita", citas);
		Usuario u = new Usuario(1, null, null, null, null, null, null, null);
		citas.setUsuario(u);
		return"redirect:/cita";
	}
	
}








