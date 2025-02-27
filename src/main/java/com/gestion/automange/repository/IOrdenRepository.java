package com.gestion.automange.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.automange.model.Orden;
import com.gestion.automange.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
	
	List<Orden> findByUsuario(Usuario usuario);

}
