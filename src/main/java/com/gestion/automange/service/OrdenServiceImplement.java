package com.gestion.automange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.repository.IOrdenRepository;

@Service
public class OrdenServiceImplement implements IOrdenService {

	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public Orden save(Orden orden) {
		// TODO Auto-generated method stub
		return ordenRepository.save(orden);
	}

	@Override
	public List<Orden> findAll() {
		// TODO Auto-generated method stub
		return ordenRepository.findAll();
	}

	@Override
	public String generarNumeroOrden() {

		int numero = 0;

		String numeroConcatenado = "";

		List<Orden> ordenes = findAll();

		List<Integer> numeros = new ArrayList<Integer>();

		// funciones java 8
		// esta es una variable anonima
		ordenes.stream().forEach(o -> numeros.add(Integer.parseInt(o.getNumero())));

		// validación
		if (ordenes.isEmpty()) {
			numero = 1;
		} else {
			numero = numeros.stream().max(Integer::compare).get();
			numero++;
		}

		if (numero < 10) {
			numeroConcatenado = "000000000000" + String.valueOf(numero);
		} else if (numero < 100) {
			numeroConcatenado = "000000000000" + String.valueOf(numero);
		} else if (numero < 1000) {
			numeroConcatenado = "000000000000" + String.valueOf(numero);
		}
		return numeroConcatenado;

	}

	@Override
	public List<Orden> findByUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		// TODO Auto-generated method stub
		return ordenRepository.findById(id);
	}

}