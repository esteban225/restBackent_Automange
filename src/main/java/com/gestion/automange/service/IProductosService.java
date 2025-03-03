package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import com.gestion.automange.model.Productos;


public interface IProductosService {


	public Productos save(Productos producto);

	public Optional<Productos> get(Integer id);

	public void update(Productos producto);

	public void delet(Integer id);

	public List<Productos> findAll();
}
