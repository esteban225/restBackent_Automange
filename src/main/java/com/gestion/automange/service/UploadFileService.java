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

	private String folder = "images//";

	// metodo para sivir la imagen del producto
	public String saveImages(MultipartFile file, String nombre) throws IOException {
		// validacion de imagenes
		if (!file.isEmpty()) {
			byte[] bytes = file.getBytes();
			// variable de tipo path que redirige al directorio
			// se importa el path de .ino.file
			Path path = Paths.get(folder + nombre + "_" + file.getOriginalFilename());
			Files.write(path, bytes);
			return nombre + "_" + file.getOriginalFilename();
		}
		return "default.jpg";
	}

	// metodo para la eliminacion de la img del producto
	public void deleteImage(String nombre) {
		String ruta = "//images";
		File file = new File(ruta + nombre);
		file.delete();
	}

}
