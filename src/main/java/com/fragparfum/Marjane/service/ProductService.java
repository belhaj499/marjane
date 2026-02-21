package com.fragparfum.Marjane.service;

import com.fragparfum.Marjane.dto.UpdateProductRequest;
import com.fragparfum.Marjane.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {
    List<Product> getAll();
    Product getById(Long id);
    Product create(Product product);
    Product update(Long id, Product product);
    void delete(Long id);
    Product updatePartial(Long id, UpdateProductRequest dto);
    Page<Product> search(String gender, Double minPrice, Double maxPrice, Pageable pageable);
    Product updateImage(Long id, String imageUrl);
}
