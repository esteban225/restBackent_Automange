package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.automange.model.Productos;
import com.gestion.automange.repository.IProductosRepository;



@Service
public class ProductosServiceImplement implements IProductosService {

	@Autowired
	private IProductosRepository productoRepository;
	
	@Override
	public Productos save(Productos producto) {
		// TODO Auto-generated method stub
		return productoRepository.save(producto);
	}

	@Override
	public Optional<Productos> get(Integer id) {
		// TODO Auto-generated method stub
		return productoRepository.findById(id);
	}

	@Override
	public void update(Productos producto) {
		// TODO Auto-generated method stub
		productoRepository.save(producto);
		
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		productoRepository.deleteById(id);
	}

	@Override
	public List<Productos> findAll() {
		// TODO Auto-generated method stub
		return productoRepository.findAll();
	}

}