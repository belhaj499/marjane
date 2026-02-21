package com.fragparfum.Marjane.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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



}
