package com.fragparfum.Marjane.service;

import com.fragparfum.Marjane.dto.UpdateProductRequest;
import com.fragparfum.Marjane.mapper.ProductMapper;
import com.fragparfum.Marjane.model.Gender;
import com.fragparfum.Marjane.model.Product;
import com.fragparfum.Marjane.repository.OrderItemRepository;
import com.fragparfum.Marjane.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repo;
    private final ProductMapper productMapper;
    private final OrderItemRepository orderItemRepository;

    public ProductServiceImpl(
            ProductRepository repo,
            ProductMapper productMapper,
            OrderItemRepository orderItemRepository
    ) {
        this.repo = repo;
        this.productMapper = productMapper;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<Product> getAll() {
        return repo.findAll();
    }

    @Override
    public Product getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public Product create(Product product) {
        return repo.save(product);
    }

    @Override
    public Product update(Long id, Product product) {
        Product exist = getById(id);
        exist.setName(product.getName());
        exist.setDescription(product.getDescription());
        exist.setPrice(product.getPrice());
        exist.setStock(product.getStock());
        exist.setVolumeMl(product.getVolumeMl());
        return repo.save(exist);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id); // check exists
        orderItemRepository.deleteByProductId(id); // 1) remove FK refs
        repo.deleteById(id);                       // 2) hard delete product
    }

    @Override
    public Product updatePartial(Long id, UpdateProductRequest dto) {
        Product exist = getById(id);
        productMapper.updateEntity(exist, dto);
        return repo.save(exist);
    }

    @Override
    public Page<Product> search(String gender, Double minPrice, Double maxPrice, Pageable pageable) {
        boolean hasGender = gender != null && !gender.isBlank();
        boolean hasMin = minPrice != null;
        boolean hasMax = maxPrice != null;

        if (hasGender && hasMin && hasMax) {
            return repo.findByGenderAndPriceBetween(
                    Gender.valueOf(gender.toUpperCase()), minPrice, maxPrice, pageable
            );
        }
        if (hasGender) {
            return repo.findByGender(Gender.valueOf(gender.toUpperCase()), pageable);
        }
        if (hasMin && hasMax) {
            return repo.findByPriceBetween(minPrice, maxPrice, pageable);
        }
        return repo.findAll(pageable);
    }

    @Override
    public Product updateImage(Long id, String imageUrl) {
        Product exist = getById(id);
        exist.setImageUrl(imageUrl);
        return repo.save(exist);
    }
}
