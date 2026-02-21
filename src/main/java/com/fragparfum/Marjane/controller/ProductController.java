package com.fragparfum.Marjane.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fragparfum.Marjane.dto.CreateProductRequest;
import com.fragparfum.Marjane.dto.ProductResponse;
import com.fragparfum.Marjane.dto.UpdateProductRequest;
import com.fragparfum.Marjane.mapper.ProductMapper;
import com.fragparfum.Marjane.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.fragparfum.Marjane.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;


    // ✅ GET ALL (client)
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

    // ✅ GET BY ID (client)
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productMapper.toResponse(
                productService.getById(id)
        );
    }

    // ✅ CREATE (admin)
    @PostMapping
    public ProductResponse create(@RequestBody CreateProductRequest dto) {
        return productMapper.toResponse(
                productService.create(
                        productMapper.toEntity(dto)
                )
        );
    }

    // ✅ UPDATE PARTIAL (admin)
    @PatchMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @RequestBody UpdateProductRequest dto) {
        return productMapper.toResponse(
                productService.updatePartial(id, dto)
        );
    }

    // ✅ DELETE (admin)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
    @PostMapping("/{id}/image")
    public ProductResponse uploadImage(@PathVariable Long id,
                                       @RequestParam("file") MultipartFile file) {

        String imageUrl = fileStorageService.save(file);
        return productMapper.toResponse(
                productService.updateImage(id, imageUrl)
        );
    }

}
