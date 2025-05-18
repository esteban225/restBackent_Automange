package com.gestion.automange.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gestion.automange.service.CustomOAuth2UserService;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailsService userDetailsService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService,
			OAuth2SuccessHandler oAuth2SuccessHandler,CustomOAuth2UserService customOAuth2UserService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userDetailsService = userDetailsService;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
		this.customOAuth2UserService = customOAuth2UserService;
	}

	/**
	 * Filtro de seguridad para OAuth2 - solo maneja rutas específicas relacionadas
	 * con autenticación.
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/oauth2/**", "/login/**", "/oauth2/authorization/**", "/oauth2/login-success",
				"/oauth2/login-failure") // ✅ Limita el alcance
				.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll()).oauth2Login(
						oauth2 -> oauth2 .userInfoEndpoint(userInfo -> userInfo
			                    .userService(customOAuth2UserService)
				                ).loginPage("/oauth2/authorization/google").successHandler(oAuth2SuccessHandler)
								.failureUrl("/api/auth/oauth2/login-failure"));

		return http.build();
	}

	/**
	 * Filtro de seguridad para API REST y WebSocket.
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/api/**", "/ws/**").csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/ws/**").permitAll()
						.requestMatchers("/api/administrador/**", "/api/productos/**", "/api/vehiculos/**",
								"/api/citas/**", "/api/mantenimientos/**", "/api/usuarios/**")
						.hasAuthority("ROLE_ADMIN").anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setExposedHeaders(List.of("Authorization"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
