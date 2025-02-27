package com.gestion.automange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.automange.model.Citas;

@Repository
public interface ICitasRepository extends JpaRepository<Citas, Integer>{

}
