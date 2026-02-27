package com.fragparfum.Marjane.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProductRequest {
    private String name;
    private String brand;
    private String gender;
    private Double price;
    private Integer stock;
    private Integer volumeMl;
    private String description;
    private String imageUrl;
    private String imagePublicId;
    private List<String> imageUrls;
    private Boolean active;
}
