package com.fragparfum.Marjane.mapper;

import com.fragparfum.Marjane.dto.CreateProductRequest;
import com.fragparfum.Marjane.dto.ProductResponse;
import com.fragparfum.Marjane.dto.UpdateProductRequest;
import com.fragparfum.Marjane.model.Gender;
import com.fragparfum.Marjane.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product p) {
        if (p == null) return null;

        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setBrand(p.getBrand());
        r.setGender(p.getGender() != null ? p.getGender().name() : null);
        r.setPrice(p.getPrice());

        r.setStock(p.getStock());
        r.setVolumeMl(p.getVolumeMl());
        r.setActive(p.getActive());

        r.setAvailable(p.getStock() != null && p.getStock() > 0);
        r.setDescription(p.getDescription());
        r.setImageUrl(p.getImageUrl());
        r.setImageUrls(
                p.getImages() == null
                        ? java.util.List.of()
                        : p.getImages().stream().map(img -> img.getUrl()).toList()
        );
        return r;
    }

    public Product toEntity(CreateProductRequest dto) {
        if (dto == null) return null;

        Product p = new Product();
        p.setName(dto.getName());
        p.setBrand(dto.getBrand());

        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            p.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        }

        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setVolumeMl(dto.getVolumeMl());
        p.setDescription(dto.getDescription());
        p.setImageUrl(dto.getImageUrl());
        p.setImagePublicId(dto.getImagePublicId());
        p.setActive(true);

        return p;
    }

    public void updateEntity(Product existing, UpdateProductRequest dto) {
        if (existing == null || dto == null) return;

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getBrand() != null) existing.setBrand(dto.getBrand());

        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            existing.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
        }

        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getStock() != null) existing.setStock(dto.getStock());
        if (dto.getVolumeMl() != null) existing.setVolumeMl(dto.getVolumeMl());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) existing.setImageUrl(dto.getImageUrl());
        if (dto.getImagePublicId() != null) existing.setImagePublicId(dto.getImagePublicId());
        if (dto.getActive() != null) existing.setActive(dto.getActive());
    }
}
