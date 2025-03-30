package com.gestion.automange.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.automange.model.RegistroMante;

@Repository
public interface IRegistroManteRepository extends JpaRepository<RegistroMante, Integer>{
	@Repository
	public interface RegistroManteRepository extends JpaRepository<RegistroMante, Integer> {
	    List<RegistroMante> findByRegistroVehiculoId(Integer vehiculoId);
	}
}
