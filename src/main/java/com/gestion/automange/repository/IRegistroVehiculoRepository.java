package com.gestion.automange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.automange.model.RegistroVehiculo;

@Repository
public interface IRegistroVehiculoRepository extends JpaRepository<RegistroVehiculo, Integer> {

}
