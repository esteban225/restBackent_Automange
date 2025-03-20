package com.gestion.automange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estadisticas")
public class Estadisticas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ventas;
    private int inventario;
    private int usuariosRegistrados;
    private int vehiculosRevisados;

    // Constructor vacío
    public Estadisticas() {
    }

    // Constructor con parámetros
    public Estadisticas(int ventas, int inventario, int usuariosRegistrados, int vehiculosRevisados) {
        this.ventas = ventas;
        this.inventario = inventario;
        this.usuariosRegistrados = usuariosRegistrados;
        this.vehiculosRevisados = vehiculosRevisados;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }

    public int getInventario() {
        return inventario;
    }

    public void setInventario(int inventario) {
        this.inventario = inventario;
    }

    public int getUsuariosRegistrados() {
        return usuariosRegistrados;
    }

    public void setUsuariosRegistrados(int usuariosRegistrados) {
        this.usuariosRegistrados = usuariosRegistrados;
    }

    public int getVehiculosRevisados() {
        return vehiculosRevisados;
    }

    public void setVehiculosRevisados(int vehiculosRevisados) {
        this.vehiculosRevisados = vehiculosRevisados;
    }
}
