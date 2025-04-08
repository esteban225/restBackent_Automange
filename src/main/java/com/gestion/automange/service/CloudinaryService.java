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

	public String uploadImage(MultipartFile file) throws IOException {
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "productos")); // opcional:
																													// crea
																													// carpeta

		return uploadResult.get("secure_url").toString(); // URL final de la imagen
	}

	public String deleteImage(String publicId) throws IOException {
		Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		return result.get("result").toString(); // "ok" si fue exitoso
	}
}
