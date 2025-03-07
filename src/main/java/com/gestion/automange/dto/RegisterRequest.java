package com.gestion.automange.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String tipo; // "ADMIN" o "USER"

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
