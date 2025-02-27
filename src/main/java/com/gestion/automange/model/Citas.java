package com.gestion.automange.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "citas")
public class Citas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Date fechaPeticion;
	private Date fechaCita;
	private String caracteristicas;

	@ManyToOne
	private Usuario usuario;


	public Citas() {

	}

	public Citas(Integer id, Date fechaPeticion, Date fechaCita, String caracteristicas, Usuario usuario) {
		super();
		this.id = id;
		this.fechaPeticion = fechaPeticion;
		this.fechaCita = fechaCita;
		this.caracteristicas = caracteristicas;
		this.usuario = usuario;
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getFechaPeticion() {
		return fechaPeticion;
	}

	public void setFechaPeticion(Date fechaPeticion) {
		this.fechaPeticion = fechaPeticion;
	}

	public Date getFechaCita() {
		return fechaCita;
	}

	public void setFechaCita(Date fechaCita) {
		this.fechaCita = fechaCita;
	}

	public String getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(String caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Citas [id=" + id + ", fechaPeticion=" + fechaPeticion + ", fechaCita=" + fechaCita
				+ ", caracteristicas=" + caracteristicas + "]";
	}
	

}
