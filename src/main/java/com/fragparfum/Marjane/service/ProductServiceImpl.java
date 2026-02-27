package com.fragparfum.Marjane.service;

import com.fragparfum.Marjane.dto.UpdateProductRequest;
import com.fragparfum.Marjane.exception.BadRequestException;
import com.fragparfum.Marjane.exception.NotFoundException;
import com.fragparfum.Marjane.mapper.ProductMapper;
import com.fragparfum.Marjane.model.Gender;
import com.fragparfum.Marjane.model.Product;
import com.fragparfum.Marjane.model.ProductImage;
import com.fragparfum.Marjane.repository.OrderItemRepository;
import com.fragparfum.Marjane.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repo;
    private final ProductMapper productMapper;
    private final OrderItemRepository orderItemRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductIntegrityValidator productIntegrityValidator;

    public ProductServiceImpl(
            ProductRepository repo,
            ProductMapper productMapper,
            OrderItemRepository orderItemRepository,
            CloudinaryService cloudinaryService,
            ProductIntegrityValidator productIntegrityValidator
    ) {
        this.repo = repo;
        this.productMapper = productMapper;
        this.orderItemRepository = orderItemRepository;
        this.cloudinaryService = cloudinaryService;
        this.productIntegrityValidator = productIntegrityValidator;
    }

    @Override
    public List<Product> getAll() {
        return repo.findAll();
    }

    @Override
    public Product getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    @Override
    public Product create(Product product) {
        productIntegrityValidator.validate(product);
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
        exist.setImageUrl(product.getImageUrl());
        exist.setImagePublicId(product.getImagePublicId());
        productIntegrityValidator.validate(exist);
        return repo.save(exist);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product p = getById(id);

        Set<String> publicIds = new LinkedHashSet<>();
        if (p.getImagePublicId() != null && !p.getImagePublicId().isBlank()) {
            publicIds.add(p.getImagePublicId());
        }
        if (p.getImages() != null) {
            p.getImages().stream()
                    .map(img -> img == null ? null : img.getPublicId())
                    .filter(pid -> pid != null && !pid.isBlank())
                    .forEach(publicIds::add);
        }

        for (String publicId : publicIds) {
            try {
                cloudinaryService.deleteByPublicId(publicId);
            } catch (Exception ex) {
                log.warn("Cloudinary delete failed for publicId={} while deleting product id={}", publicId, id, ex);
            }
        }

        orderItemRepository.deleteByProductId(id);
        repo.deleteById(id);
    }

    @Override
    public Product updatePartial(Long id, UpdateProductRequest dto) {
        if (dto != null && dto.getDescription() != null) {
            productIntegrityValidator.validateDescription(dto.getDescription());
        }

        Product exist = getById(id);
        productMapper.updateEntity(exist, dto);

        if (dto != null && dto.getImageUrls() != null) {
            List<String> requested = dto.getImageUrls().stream()
                    .map(url -> url == null ? "" : url.trim())
                    .filter(url -> !url.isBlank())
                    .collect(Collectors.toList());

            List<ProductImage> existingImages = exist.getImages() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(exist.getImages());

            Map<String, ProductImage> byUrl = new LinkedHashMap<>();
            for (ProductImage image : existingImages) {
                if (image == null || image.getUrl() == null) continue;
                byUrl.put(image.getUrl(), image);
            }

            Set<String> requestedSet = new LinkedHashSet<>(requested);
            List<ProductImage> removed = existingImages.stream()
                    .filter(image -> image != null && image.getUrl() != null && !requestedSet.contains(image.getUrl()))
                    .toList();

            for (ProductImage image : removed) {
                String publicId = image.getPublicId();
                if (publicId == null || publicId.isBlank()) continue;
                try {
                    cloudinaryService.deleteByPublicId(publicId);
                } catch (Exception ex) {
                    log.warn("Cloudinary delete failed for publicId={} while updating product id={}", publicId, id, ex);
                }
            }

            List<ProductImage> nextImages = new ArrayList<>();
            for (String url : requested) {
                ProductImage known = byUrl.get(url);
                if (known != null) {
                    nextImages.add(known);
                }
            }
            if (exist.getImages() == null) {
                exist.setImages(new ArrayList<>());
            }
            exist.getImages().clear();
            exist.getImages().addAll(nextImages);

            if (exist.getImages().isEmpty()) {
                exist.setImageUrl(null);
                exist.setImagePublicId(null);
            } else {
                Set<String> nextUrls = exist.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toSet());
                boolean mainStillExists = exist.getImageUrl() != null && nextUrls.contains(exist.getImageUrl());
                if (!mainStillExists) {
                    ProductImage first = exist.getImages().get(0);
                    exist.setImageUrl(first.getUrl());
                    exist.setImagePublicId(first.getPublicId());
                }
            }
        }

        productIntegrityValidator.validate(exist);
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
    public Product updateImage(Long id, String imageUrl, String imagePublicId) {
        Product exist = getById(id);
        exist.setImageUrl(imageUrl);
        exist.setImagePublicId(imagePublicId);
        productIntegrityValidator.validateMainImagePair(exist.getImageUrl(), exist.getImagePublicId());
        return repo.save(exist);
    }

    @Override
    public Product save(Product product) {
        productIntegrityValidator.validate(product);
        return repo.save(product);
    }
}
