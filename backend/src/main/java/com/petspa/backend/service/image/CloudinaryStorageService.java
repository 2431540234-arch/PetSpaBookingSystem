package com.petspa.backend.service.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.petspa.backend.exception.image.ImageUploadException;
import com.petspa.backend.exception.image.InvalidImageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
public class CloudinaryStorageService implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryStorageService.class);
    private final Cloudinary cloudinary;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public CloudinaryStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map<String, Object> upload(MultipartFile file, String folder, int width, int height) {
        validateImage(file);
        try {
            Map params = ObjectUtils.asMap(
                    "folder", "petspa/" + folder,
                    "transformation", new Transformation<>().width(width).height(height).crop("fill").gravity("face"),
                    "resource_type", "image"
            );
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            log.info("Uploaded image to Cloudinary: {}", uploadResult.get("url"));
            return uploadResult;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new ImageUploadException("Could not upload image: " + e.getMessage());
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", publicId, e);
        }
    }

    @Override
    public Map<String, Object> replace(String publicId, MultipartFile file, String folder, int width, int height) {
        if (publicId != null && !publicId.isEmpty()) {
            delete(publicId);
        }
        return upload(file, folder, width, height);
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidImageException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException("File size exceeds 10MB limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException("Unsupported image format. Allowed: JPG, PNG, WEBP");
        }
    }
}
