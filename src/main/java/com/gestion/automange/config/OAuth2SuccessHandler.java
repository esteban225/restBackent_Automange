package com.gestion.automange.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtProvider jwtService;

	public OAuth2SuccessHandler(JwtProvider jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                     Authentication authentication) throws IOException, ServletException {
		
		

	    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
	    String username = oAuth2User.getAttribute("email");
	    List<String> roles = authentication.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .toList();

	    String jwt = jwtService.generateToken(username, roles);

	    // 🔁 Redirigir a Angular con el token
	    String redirectUrl = "http://localhost:4200/#/login-success?token=" + jwt;
	    response.sendRedirect(redirectUrl);
	}


}
