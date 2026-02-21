package com.fragparfum.Marjane.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String customerName;
    private String phone;
    private String address;
    private String status;
    private Double total;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
