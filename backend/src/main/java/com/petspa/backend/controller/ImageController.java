package com.petspa.backend.controller;

import com.petspa.backend.service.image.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/images")
@Tag(name = "Image API")
public class ImageController {

    private final ImageStorageService imageStorageService;

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @Operation(summary = "Upload ảnh lên Cloudinary")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {

        int width = 1024;
        int height = 1024;
        String folder = "general";

        if ("avatar".equalsIgnoreCase(type)) {
            width = 512;
            height = 512;
            folder = "avatars";
        } else if ("pet".equalsIgnoreCase(type)) {
            folder = "pets";
        } else if ("service".equalsIgnoreCase(type)) {
            folder = "services";
        }

        Map<String, Object> result = imageStorageService.upload(file, folder, width, height);
        
        Map<String, String> response = new HashMap<>();
        response.put("url", result.get("url").toString());
        response.put("publicId", result.get("public_id").toString());
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa ảnh trên Cloudinary")
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam("publicId") String publicId) {
        imageStorageService.delete(publicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Thay thế ảnh cũ bằng ảnh mới")
    @PutMapping("/replace")
    public ResponseEntity<Map<String, String>> replaceImage(
            @RequestParam("publicId") String publicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {

        int width = 1024;
        int height = 1024;
        String folder = "general";

        if ("avatar".equalsIgnoreCase(type)) {
            width = 512;
            height = 512;
            folder = "avatars";
        }

        Map<String, Object> result = imageStorageService.replace(publicId, file, folder, width, height);

        Map<String, String> response = new HashMap<>();
        response.put("url", result.get("url").toString());
        response.put("publicId", result.get("public_id").toString());

        return ResponseEntity.ok(response);
    }
}
