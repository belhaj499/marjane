package com.fragparfum.Marjane.dto;

import lombok.Data;

@Data
public class CreateProductRequest {

    private String name;
    private String brand;
    private String gender;
    private Double price;
    private Integer stock;
    private Integer volumeMl;
    private String description;
    private String imageUrl;
    private String imagePublicId;
}
