package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.repository.IRegistroManteRepository;

@Service
public class RegistroManteServiceImplement implements IRegistroManteService {
	
	@Autowired
	private IRegistroManteRepository registroManteRepository;
	
	@Override
	public RegistroMante save(RegistroMante registroMante) {
		return registroManteRepository.save(registroMante);
		
	}
	
	@Override
	public Optional<RegistroMante> get(Integer id){
		return registroManteRepository.findById(id);
	}
	
	@Override
	public void update(RegistroMante registroMante) {
		registroManteRepository.save(registroMante);
	} 

	@Override
	public void delet(Integer id) {
		registroManteRepository.deleteById(id);
	}
	
	@Override
	public List<RegistroMante> findAll(){
		return registroManteRepository.findAll();
	}
}
