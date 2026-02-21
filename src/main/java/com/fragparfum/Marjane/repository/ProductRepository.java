package com.fragparfum.Marjane.repository;

import com.fragparfum.Marjane.model.Gender;
import com.fragparfum.Marjane.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1) غير Gender + Pagination
    Page<Product> findByGender(Gender gender, Pageable pageable);

    // 2) Gender + min/max price + Pagination
    Page<Product> findByGenderAndPriceBetween(Gender gender, Double minPrice, Double maxPrice, Pageable pageable);

    // 3) price فقط (إلا ما عطاش gender)
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
}
