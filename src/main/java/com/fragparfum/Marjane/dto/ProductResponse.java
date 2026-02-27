package com.fragparfum.Marjane.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private String gender;
    private Double price;

    private Integer stock;
    private Integer volumeMl;
    private Boolean active;

    private Boolean available;
    private String description;

    private String imageUrl;
    private List<String> imageUrls = new ArrayList<>();
}
