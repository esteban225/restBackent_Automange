package com.gestion.automange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registrosMante")
public class RegistroMante {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String fechaMante;
	private String nombre;
	private String caracteristrica;
	private String imagen;
	private Integer precio;

	@ManyToOne
	private RegistroVehiculo registroVehiculo;

	@ManyToOne
	public Citas citas;

	public RegistroMante() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegistroMante(Integer id, String fechaMante, String nombre, String caracteristrica, String imagen,
			Integer precio, RegistroVehiculo registroVehiculo, Citas citas) {
		super();
		this.id = id;
		this.fechaMante = fechaMante;
		this.nombre = nombre;
		this.caracteristrica = caracteristrica;
		this.imagen = imagen;
		this.precio = precio;
		this.registroVehiculo = registroVehiculo;
		this.citas = citas;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFechaMante() {
		return fechaMante;
	}

	public void setFechaMante(String fechaMante) {
		this.fechaMante = fechaMante;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCaracteristrica() {
		return caracteristrica;
	}

	public void setCaracteristrica(String caracteristrica) {
		this.caracteristrica = caracteristrica;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public Integer getPrecio() {
		return precio;
	}

	public void setPrecio(Integer precio) {
		this.precio = precio;
	}

	public RegistroVehiculo getRegistroVehiculo() {
		return registroVehiculo;
	}

	public void setRegistroVehiculo(RegistroVehiculo registroVehiculo) {
		this.registroVehiculo = registroVehiculo;
	}

	public Citas getCitas() {
		return citas;
	}

	public void setCitas(Citas citas) {
		this.citas = citas;
	}

	@Override
	public String toString() {
		return "RegistroMante [id=" + id + ", fechaMante=" + fechaMante + ", nombre=" + nombre + ", caracteristrica="
				+ caracteristrica + ", imagen=" + imagen + ", precio=" + precio + "]";
	}
	
}