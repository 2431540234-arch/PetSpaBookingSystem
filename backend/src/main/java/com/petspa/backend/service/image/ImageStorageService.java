package com.petspa.backend.service.image;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ImageStorageService {
    Map<String, Object> upload(MultipartFile file, String folder, int width, int height);
    void delete(String publicId);
    Map<String, Object> replace(String publicId, MultipartFile file, String folder, int width, int height);
}
