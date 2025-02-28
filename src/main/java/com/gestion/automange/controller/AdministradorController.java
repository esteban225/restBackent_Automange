package com.gestion.automange.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gestion.automange.service.IProductosService;
import com.gestion.automange.model.Productos;


@Controller
@RequestMapping("/administrador")
public class AdministradorController  {
	
	@Autowired
	private IProductosService productosService;
	
	@GetMapping("")
	
	public String home(Model model) {
		List<Productos> productos = productosService.findAll();
		model.addAttribute("productos",productos);
		return "administrador/home";
	}
}
