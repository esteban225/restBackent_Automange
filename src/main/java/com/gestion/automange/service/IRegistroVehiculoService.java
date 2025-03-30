package com.gestion.automange.service;

import java.util.List;
import java.util.Optional;

import com.gestion.automange.model.RegistroMante;
import com.gestion.automange.model.RegistroVehiculo;

public interface IRegistroVehiculoService {

	public RegistroVehiculo save(RegistroVehiculo registroVehiculo);

	public Optional<RegistroVehiculo> get(Integer id);

	public void update(RegistroVehiculo registroVehiculo);

	public void delet(Integer id);

	List<RegistroVehiculo> findAll();

	public void setImagen(String nombreImagen);

	public Optional<RegistroMante> findById(Integer vehiculoId);

}
