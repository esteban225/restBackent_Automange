package com.gestion.automange.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
public class RegistroVehiculo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Integer Id;
	private String nombre;
	private String marca;
	private Integer modelo;
	private Integer cilindraje;
	private String placa;
	private String imagen;

	@ManyToOne
	private Usuario usuario;

	@OneToMany(mappedBy = "registroVehiculo")
	private List<RegistroMante> registroMante;

	@OneToMany(mappedBy = "registroVehiculo")
	private List<Citas> citas;
	public RegistroVehiculo() {

	}

	public RegistroVehiculo(Integer id, String nombre, String marca, Integer modelo, Integer cilindraje, String placa,
			String imagen, Usuario usuario, List<RegistroMante> registroMante) {
		super();
		Id = id;
		this.nombre = nombre;
		this.marca = marca;
		this.modelo = modelo;
		this.cilindraje = cilindraje;
		this.placa = placa;
		this.imagen = imagen;
		this.usuario = usuario;
		this.registroMante = registroMante;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Integer getModelo() {
		return modelo;
	}

	public void setModelo(Integer modelo) {
		this.modelo = modelo;
	}

	public Integer getCilindraje() {
		return cilindraje;
	}

	public void setCilindraje(Integer cilindraje) {
		this.cilindraje = cilindraje;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<RegistroMante> getRegistroMante() {
		return registroMante;
	}

	public void setRegistroMante(List<RegistroMante> registroMante) {
		this.registroMante = registroMante;
	}

	@Override
	public String toString() {
		return "RegistroVehiculo [Id=" + Id + ", nombre=" + nombre + ", marca=" + marca + ", modelo=" + modelo
				+ ", cilindraje=" + cilindraje + ", placa=" + placa + ", imagen=" + imagen + "]";
	}
	
	
	
}