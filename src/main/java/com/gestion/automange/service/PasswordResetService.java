package com.gestion.automange.service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.gestion.automange.model.Usuario;
import com.gestion.automange.repository.IUsuarioRepository;

@Service
public class PasswordResetService {

	@Autowired
	private IUsuarioRepository usuarioRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; // Inyección del encriptador

	public void sendResetEmail(String email) throws MessagingException {
		if (email == null || email.isBlank()) {
			throw new RuntimeException("El correo no puede estar vacío");
		}

		Usuario user = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Correo no encontrado"));

		String token = UUID.randomUUID().toString();
		user.setResetToken(token);
		usuarioRepository.save(user);

		String resetUrl = "https://frontend-automange.vercel.app/#/reset-password?token=" + token;
		String htmlContent = getEmailTemplate(resetUrl);

		// Enviar el correo con HTML
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

		helper.setTo(email);
		helper.setSubject("Recuperación de contraseña");
		helper.setText(htmlContent, true);

		mailSender.send(message);
	}

	private String getEmailTemplate(String resetLink) {
		return """
				<!DOCTYPE html>
				<html lang="es">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>Recuperación de Contraseña</title>
				    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
				    <style>
				        body {
				            font-family: Arial, sans-serif;
				            background-color: #f4f4f4;
				            padding: 20px;
				        }
				        .container {
				            max-width: 500px;
				            background: #ffffff;
				            padding: 20px;
				            border-radius: 10px;
				            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
				            text-align: center;
				            margin: auto;
				        }
				        .btn-custom {
				            background-color: #6a0dad;
				            color: white !important;
				            font-weight: bold;
				            padding: 12px 20px;
				            border-radius: 8px;
				            display: inline-block;
				            text-decoration: none;
				            transition: 0.3s ease-in-out;
				        }
				        .btn-custom:hover {
				            background-color: #520b99;
				            transform: scale(1.05);
				        }
				        .footer {
				            margin-top: 20px;
				            font-size: 0.9em;
				            color: #555;
				        }
				    </style>
				</head>
				<body>
				    <div class="container">
				        <h2 class="text-primary">Recuperación de Contraseña</h2>
				        <p>Hola,</p>
				        <p>Has solicitado restablecer tu contraseña. Para continuar, haz clic en el siguiente botón:</p>
				        <a href="%s" class="btn btn-custom">Restablecer Contraseña</a>
				        <p class="footer">Si no solicitaste este cambio, ignora este mensaje.</p>
				        <p><strong>Equipo de AutoManage Pro</strong></p>
				    </div>
				</body>
				</html>


												"""
				.formatted(resetLink); // 🛠 Aquí se inserta el resetLink correctamente
	}

	// Método para restablecer la contraseña
	public void resetPassword(String token, String newPassword) {
		// Buscar al usuario por el token
		Usuario user = usuarioRepository.findByResetToken(token)
				.orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

		// Encriptar la nueva contraseña antes de guardarla
		String hashedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(hashedPassword);

		// Eliminar el token de recuperación
		user.setResetToken(null);
		usuarioRepository.save(user);
	}

}
