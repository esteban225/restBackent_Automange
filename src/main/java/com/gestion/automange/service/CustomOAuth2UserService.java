package com.gestion.automange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.gestion.automange.config.CustomOAuth2User;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.repository.IUsuarioRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Autowired
	private IUsuarioRepository usuarioRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");

		if (email == null) {
			throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
		}

		Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setEmail(email);
			nuevoUsuario.setNombre(name);
			nuevoUsuario.setUsername(email);
			nuevoUsuario.setRole("USER");
			nuevoUsuario.setActive(true);
			nuevoUsuario.setPassword("");
			return usuarioRepository.save(nuevoUsuario);
		});

		return new CustomOAuth2User(usuario, oAuth2User.getAttributes());
	}
}
