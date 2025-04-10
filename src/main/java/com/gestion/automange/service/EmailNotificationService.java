package com.gestion.automange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendRegistrationSuccessEmail(String to, String nombre) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("¡Bienvenido a AutoManage!");
		message.setText("Hola " + nombre
				+ ",\n\nTu registro fue exitoso. ¡Gracias por unirte a AutoManage!\n\nSaludos,\nEquipo AutoManage");
		mailSender.send(message);
	}
}
