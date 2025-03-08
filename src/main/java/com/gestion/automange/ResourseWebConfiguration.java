package com.gestion.automange;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourseWebConfiguration implements WebMvcConfigurer {

	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// ruta abspluta para windows
		String externalPath = "file:C:/images/";
		registry.addResourceHandler("/images/**").addResourceLocations(externalPath);

		// ruta de prueba de desarrollo
		// registry.addResourceHandler("/images/**").addResourceLocations("file:images/");
	}

}
