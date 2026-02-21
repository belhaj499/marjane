package com.fragparfum.Marjane.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private String gender;
    private Double price;

    private Integer stock;      // مهم للإدارة
    private Integer volumeMl;   // مهم للإدارة
    private Boolean active;     // مهم للإدارة

    private Boolean available;  // للclient
    private String description;
    private String imageUrl;
}
