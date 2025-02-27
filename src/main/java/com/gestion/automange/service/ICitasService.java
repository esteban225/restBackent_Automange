package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import com.gestion.automange.model.Citas;

public interface ICitasService {

public Citas save(Citas citas);
	
	public Optional<Citas> get(Integer id);
	
	public void update(Citas citas);
	
	public void delet(Integer id);
	
	public List<Citas> findAll();

}
