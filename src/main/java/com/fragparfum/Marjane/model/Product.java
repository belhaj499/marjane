package com.fragparfum.Marjane.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Double price;

    private Integer stock;

    private Integer volumeMl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean active = true;
    private String imageUrl;
    private String imagePublicId;

    @ElementCollection
    @CollectionTable(name = "product_gallery_images", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "sort_order")
    private List<ProductImage> images = new ArrayList<>();
}
