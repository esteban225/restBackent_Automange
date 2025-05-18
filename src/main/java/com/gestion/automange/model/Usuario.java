package com.gestion.automange.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	private String username;
	private String email;
	private String direccion;
	private String telefono;
	private String role;
	private Boolean active;// rol de ususario
	@JsonIgnore
	private String password;
	 private String resetToken;


	@OneToMany(mappedBy = "usuario")
	@JsonManagedReference("usuario-productos")
	private List<Productos> productos;

	@OneToMany(mappedBy = "usuario")
	@JsonManagedReference("usuario-vehiculos")
	private List<RegistroVehiculo> registroVehiculo;

	@OneToMany(mappedBy = "usuario")
	@JsonManagedReference("usuario-ordenes")
	private List<Orden> ordenes;

	@OneToMany(mappedBy = "usuario")
	@JsonManagedReference("usuario-citas")
	private List<Citas> citas;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	// constructor sin campos
	public Usuario() {

	}

	// constructor con campos

	public Usuario(Integer id, String nombre, String username, String email, String direccion, String telefono,
			String role, String password) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.username = username;
		this.email = email;
		this.direccion = direccion;
		this.telefono = telefono;
		this.role = role;
		this.password = password;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getRole(String user) {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", username=" + username + ", email=" + email
				+ ", direccion=" + direccion + ", telefono=" + telefono + ", role=" + role + ", password=" + password
				+ "]";
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

}