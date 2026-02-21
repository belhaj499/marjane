package com.fragparfum.Marjane.dto;

import lombok.Data;

@Data
public class CreateProductRequest {

    private String name;
    private String brand;
    private String gender; // "HOMME" / "FEMME" / "UNISEX"
    private Double price;
    private Integer stock;
    private Integer volumeMl;
    private String description;
    private String imageUrl; // تقدر تبقى null حتى تدير upload
}
