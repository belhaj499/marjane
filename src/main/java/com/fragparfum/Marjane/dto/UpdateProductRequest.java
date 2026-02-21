package com.fragparfum.Marjane.dto;

import lombok.Data;

@Data
public class UpdateProductRequest {
    private String name;
    private String brand;
    private String gender;      // HOMME / FEMME / UNISEX
    private Double price;
    private Integer stock;
    private Integer volumeMl;
    private String description;
    private String imageUrl;
    private Boolean active;     // مهم
}
