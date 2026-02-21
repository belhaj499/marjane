package com.fragparfum.Marjane.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String original = file.getOriginalFilename();
            String ext = "";

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String filename = UUID.randomUUID() + ext;
            Path dest = root.resolve(filename);

            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            // هاد الرابط اللي غادي نخرّجوه للـ client
            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
