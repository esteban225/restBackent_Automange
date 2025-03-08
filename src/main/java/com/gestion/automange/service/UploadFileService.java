package com.gestion.automange.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {

	// Ruta absoluta para Windows (sin "file:")
	private final String storagePath = "C:/images/";

	// Método para subir la imagen
	public String saveImages(MultipartFile file, String nombre) throws IOException {
		if (!file.isEmpty()) {
			byte[] bytes = file.getBytes();

			// Construcción correcta de la ruta
			Path path = Paths.get(storagePath, nombre + "_" + file.getOriginalFilename());

			// Crear la carpeta si no existe
			File directory = new File(storagePath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			// Escribir archivo en disco
			Files.write(path, bytes);

			return nombre + "_" + file.getOriginalFilename();
		}
		return "default.jpg";
	}

	// Método para eliminar la imagen
	public void deleteImage(String nombre) {
		String ruta = storagePath + nombre;
		File file = new File(ruta);
		if (file.exists()) {
			file.delete();
		}
	}
}
