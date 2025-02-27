package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.automange.model.Citas;
import com.gestion.automange.repository.ICitasRepository;

@Service
public class CitasServiceImplement implements ICitasService {
	
	@Autowired
	private ICitasRepository citasRepository;
	
	@Override
	public Citas save(Citas citas) {
		return citasRepository.save(citas);
	}
	
	@Override
	public Optional<Citas> get(Integer id){
		return citasRepository.findById(id);
	}
	
	@Override
	public void update(Citas citas) {
		citasRepository.save(citas);
	}
	
	@Override
	public void delet(Integer id) {
		citasRepository.deleteById(id);
		
	}
	
	@Override
	public List<Citas> findAll(){
		return citasRepository.findAll();
	}
}
