package com.fragparfum.Marjane.dto;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double lineTotal;
    private String imageUrl;
}
