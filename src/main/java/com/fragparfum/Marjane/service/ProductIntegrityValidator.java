package com.fragparfum.Marjane.service;

import com.fragparfum.Marjane.exception.BadRequestException;
import com.fragparfum.Marjane.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductIntegrityValidator {

    public void validate(Product product) {
        if (product == null) return;
        validateDescription(product.getDescription());
        validateMainImagePair(product.getImageUrl(), product.getImagePublicId());
    }

    public void validateDescription(String description) {
        if (description != null && description.contains("[[IMAGES:")) {
            throw new BadRequestException(
                    "Invalid description: image metadata ([[IMAGES:...]] ) is not allowed."
            );
        }
    }

    public void validateMainImagePair(String imageUrl, String imagePublicId) {
        boolean hasUrl = imageUrl != null && !imageUrl.isBlank();
        boolean hasPublicId = imagePublicId != null && !imagePublicId.isBlank();
        if (hasUrl != hasPublicId) {
            throw new BadRequestException(
                    "Main image is invalid: both imageUrl and imagePublicId are required together."
            );
        }
    }
}
