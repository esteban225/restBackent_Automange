package com.gestion.automange.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

	@Autowired
	private Cloudinary cloudinary;

	/**
	 * Sube una imagen a una carpeta específica (productos, vehiculos,
	 * mantenimientos).
	 *
	 * @param file          Archivo de imagen a subir.
	 * @param nombreEntidad Nombre de la entidad (usado para nombrar la imagen).
	 * @param tipo          Tipo de carpeta: productos, vehiculos, mantenimientos.
	 * @return URL segura de la imagen subida.
	 */
	public String uploadImage(MultipartFile file, String nombreEntidad, String tipo) throws IOException {
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", tipo.toLowerCase(), // crea
																															// una
																															// carpeta
																															// específica
																															// según
																															// tipo
				"use_filename", true, "unique_filename", true, "public_id",
				nombreEntidad.replaceAll("\\s+", "_").toLowerCase()));

		return uploadResult.get("secure_url").toString();
	}

	/**
	 * Elimina una imagen de Cloudinary usando su URL completa.
	 *
	 * @param imageUrl URL completa de la imagen a eliminar.
	 * @return Resultado de la operación ("ok" si fue exitosa).
	 */
	public String deleteImage(String imageUrl) throws IOException {
		// Extraer el public_id a partir de la URL
		String publicId = extractPublicIdFromUrl(imageUrl);
		Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		return result.get("result").toString();
	}

	/**
	 * Extrae el public_id desde la URL de Cloudinary.
	 *
	 * @param url URL completa de Cloudinary.
	 * @return public_id que se necesita para eliminar la imagen.
	 */
	private String extractPublicIdFromUrl(String url) {
		// Ejemplo de URL:
		// https://res.cloudinary.com/demo/image/upload/v1234567890/productos/imagen.jpg
		String[] parts = url.split("/");
		String fileName = parts[parts.length - 1]; // imagen.jpg
		String folder = parts[parts.length - 2]; // productos, vehiculos, mantenimientos
		String publicId = folder + "/" + fileName.split("\\.")[0]; // productos/imagen
		return publicId;
	}
}
