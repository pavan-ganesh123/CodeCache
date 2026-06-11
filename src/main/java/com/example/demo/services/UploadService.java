package com.example.demo.services;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image (JPEG, PNG, GIF, WebP)");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5_000_000) {
            throw new IllegalArgumentException("Image size must be less than 5MB");
        }

        try {
            // Upload to Cloudinary
            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "image");
            
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            
            // Return secure URL
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }
}