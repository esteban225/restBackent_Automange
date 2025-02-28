package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.repository.IRegistroVehiculoRepository;

@Service
public class RegistroVehiculoServiceImplement implements IRegistroVehiculoService {

	@Autowired
	private IRegistroVehiculoRepository registroVehiculoRepository;

	@Override
	public RegistroVehiculo save(RegistroVehiculo registroVehiculo) {
		// TODO Auto-generated method stub
		return registroVehiculoRepository.save(registroVehiculo);
	}

	@Override
	public Optional<RegistroVehiculo> get(Integer id) {
		// TODO Auto-generated method stub
		return registroVehiculoRepository.findById(id);
	}

	@Override
	public void update(RegistroVehiculo registroVehiculo) {
		// TODO Auto-generated method stub
		registroVehiculoRepository.save(registroVehiculo);
	}

	@Override
	public void delet(Integer id) {
		// TODO Auto-generated method stub
		registroVehiculoRepository.deleteById(id);
	}

	@Override
	public List<RegistroVehiculo> findAll() {
		// TODO Auto-generated method stub
		return registroVehiculoRepository.findAll();
	}

	@Override
	public void setImagen(String nombreImagen) {
		// TODO Auto-generated method stub

	}

}
