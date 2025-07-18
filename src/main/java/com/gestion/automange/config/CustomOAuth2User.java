package com.gestion.automange.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.gestion.automange.model.Usuario;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

	private Usuario usuario;
	private Map<String, Object> attributes;

	public CustomOAuth2User(Usuario usuario, Map<String, Object> attributes) {
		this.usuario = usuario;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole(null)));
	}

	@Override
	public String getName() {
		return usuario.getEmail(); // o ID si prefieres
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
