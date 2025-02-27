package com.gestion.automange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.automange.model.Productos;

@Repository
public interface IProductosRepository extends JpaRepository<Productos, Integer>{

}
