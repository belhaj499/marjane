package com.fragparfum.Marjane.controller;

import com.fragparfum.Marjane.dto.CreateProductRequest;
import com.fragparfum.Marjane.dto.ProductResponse;
import com.fragparfum.Marjane.dto.UpdateProductImagesRequest;
import com.fragparfum.Marjane.dto.UpdateProductRequest;
import com.fragparfum.Marjane.mapper.ProductMapper;
import com.fragparfum.Marjane.model.Product;
import com.fragparfum.Marjane.model.ProductImage;
import com.fragparfum.Marjane.service.CloudinaryService;
import com.fragparfum.Marjane.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public Page<ProductResponse> getAll(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable
    ) {
        return productService.search(gender, minPrice, maxPrice, pageable)
                .map(productMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productMapper.toResponse(productService.getById(id));
    }

    @PostMapping
    public ProductResponse create(@RequestBody CreateProductRequest dto) {
        return productMapper.toResponse(productService.create(productMapper.toEntity(dto)));
    }

    @PatchMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @RequestBody UpdateProductRequest dto) {
        return productMapper.toResponse(productService.updatePartial(id, dto));
    }

    @PutMapping("/{id}")
    public ProductResponse updatePut(@PathVariable Long id,
                                     @RequestBody UpdateProductRequest dto) {
        return productMapper.toResponse(productService.updatePartial(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ProductResponse uploadImage(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file) {

        var upload = cloudinaryService.uploadImage(file);

        Product exist = productService.getById(id);
        exist.setImageUrl(upload.getUrl());
        exist.setImagePublicId(upload.getPublicId());

        return productMapper.toResponse(productService.save(exist));
    }

    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    public ProductResponse uploadMany(@PathVariable Long id,
                                      @RequestParam("files") MultipartFile[] files) {

        Product p = productService.getById(id);

        for (MultipartFile f : files) {
            var up = cloudinaryService.uploadImage(f);
            p.getImages().add(new ProductImage(up.getUrl(), up.getPublicId()));
        }

        if ((p.getImageUrl() == null || p.getImageUrl().isBlank()) && !p.getImages().isEmpty()) {
            ProductImage first = p.getImages().get(0);
            p.setImageUrl(first.getUrl());
            p.setImagePublicId(first.getPublicId());
        }

        return productMapper.toResponse(productService.save(p));
    }

    @PatchMapping("/{id}/images")
    public ProductResponse syncImages(@PathVariable Long id,
                                      @RequestBody UpdateProductImagesRequest dto) {
        return syncImagesInternal(id, dto);
    }

    @PutMapping("/{id}/images")
    public ProductResponse syncImagesPut(@PathVariable Long id,
                                         @RequestBody UpdateProductImagesRequest dto) {
        return syncImagesInternal(id, dto);
    }

    @PostMapping("/{id}/images/sync")
    public ProductResponse syncImagesPost(@PathVariable Long id,
                                          @RequestBody UpdateProductImagesRequest dto) {
        return syncImagesInternal(id, dto);
    }

    private ProductResponse syncImagesInternal(Long id, UpdateProductImagesRequest dto) {
        Product p = productService.getById(id);

        List<String> requested = dto == null || dto.getImageUrls() == null
                ? List.of()
                : dto.getImageUrls().stream()
                .map(url -> url == null ? "" : url.trim())
                .filter(url -> !url.isBlank())
                .collect(Collectors.toList());

        Set<String> requestedSet = new LinkedHashSet<>(requested);
        List<ProductImage> existing = p.getImages() == null ? new ArrayList<>() : new ArrayList<>(p.getImages());

        List<ProductImage> nextImages = existing.stream()
                .filter(img -> img != null && requestedSet.contains(img.getUrl()))
                .collect(Collectors.toCollection(ArrayList::new));

        List<ProductImage> removed = existing.stream()
                .filter(img -> img != null && !requestedSet.contains(img.getUrl()))
                .toList();

        for (ProductImage image : removed) {
            String publicId = image.getPublicId();
            if (publicId == null || publicId.isBlank()) continue;
            try {
                cloudinaryService.deleteByPublicId(publicId);
            } catch (Exception ignored) {
            }
        }

        p.setImages(nextImages);

        if (nextImages.isEmpty()) {
            p.setImageUrl(null);
            p.setImagePublicId(null);
        } else {
            Set<String> nextUrls = nextImages.stream()
                    .map(ProductImage::getUrl)
                    .collect(Collectors.toSet());
            boolean mainStillExists = p.getImageUrl() != null && nextUrls.contains(p.getImageUrl());
            if (!mainStillExists) {
                ProductImage first = nextImages.get(0);
                p.setImageUrl(first.getUrl());
                p.setImagePublicId(first.getPublicId());
            }
        }

        return productMapper.toResponse(productService.save(p));
    }
}
