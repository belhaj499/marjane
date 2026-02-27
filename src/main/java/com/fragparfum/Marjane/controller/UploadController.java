package com.fragparfum.Marjane.controller;

import com.fragparfum.Marjane.dto.CloudinaryUploadResult;
import com.fragparfum.Marjane.service.CloudinaryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        CloudinaryUploadResult result = cloudinaryService.uploadImage(file);

        return ResponseEntity.ok(Map.of(
                "url", result.getUrl(),
                "publicId", result.getPublicId()
        ));
    }
}
