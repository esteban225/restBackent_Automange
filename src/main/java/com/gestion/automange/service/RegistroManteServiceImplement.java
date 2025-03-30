package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.automange.dto.RegistroManteDTO;
import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.model.RegistroVehiculo;
import com.gestion.automange.repository.IRegistroManteRepository;
import com.gestion.automange.repository.IRegistroVehiculoRepository;

@Service
public class RegistroManteServiceImplement implements IRegistroManteService {
	
	@Autowired
	private IRegistroManteRepository registroManteRepository;
	
	@Autowired
	private IRegistroVehiculoRepository registroVehiculoRepository;
	
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
	
    public RegistroMante crearMantenimiento(RegistroManteDTO registroManteDTO) {
        RegistroVehiculo vehiculo = registroVehiculoRepository.findById(registroManteDTO.getVehiculoId())
            .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        RegistroMante mantenimiento = new RegistroMante();
        mantenimiento.setFechaMante(registroManteDTO.getFechaMante());
        mantenimiento.setNombre(registroManteDTO.getNombre());
        mantenimiento.setCaracteristrica(registroManteDTO.getCaracteristica());
        mantenimiento.setImagen(registroManteDTO.getImagen());
        mantenimiento.setPrecio(registroManteDTO.getPrecio());
        mantenimiento.setRegistroVehiculo(vehiculo);

        return registroManteRepository.save(mantenimiento);
    }
}
