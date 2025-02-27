package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;
import com.gestion.automange.model.RegistroMante;

public interface IRegistroManteService {

public RegistroMante save(RegistroMante registroMante);
	
	public Optional<RegistroMante> get(Integer id);
	
	public void update(RegistroMante registroMante);
	
	public void delet(Integer id);
	
	public List<RegistroMante> findAll();
}

