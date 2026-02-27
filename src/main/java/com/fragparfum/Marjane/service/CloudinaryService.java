package com.fragparfum.Marjane.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fragparfum.Marjane.dto.CloudinaryUploadResult;
import com.fragparfum.Marjane.exception.BadRequestException;
import com.fragparfum.Marjane.exception.CloudinaryOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    public CloudinaryUploadResult uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required.");
        }

        try {
            Map<?, ?> res = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );

            String url = stringValue(res.get("secure_url"));
            String publicId = stringValue(res.get("public_id"));

            if (url == null || url.isBlank() || publicId == null || publicId.isBlank()) {
                throw new CloudinaryOperationException("Cloudinary upload response is missing url or publicId.");
            }

            return new CloudinaryUploadResult(url, publicId);
        } catch (IOException e) {
            throw new CloudinaryOperationException("Cloudinary upload failed.", e);
        }
    }

    public boolean deleteByPublicId(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return false;
        }

        try {
            Map<?, ?> res = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
            String result = stringValue(res.get("result"));
            return "ok".equalsIgnoreCase(result) || "not found".equalsIgnoreCase(result);
        } catch (IOException e) {
            throw new CloudinaryOperationException("Cloudinary delete failed for publicId: " + publicId, e);
        }
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
