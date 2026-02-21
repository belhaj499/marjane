package com.fragparfum.Marjane.dto;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String status; // "CONFIRMED" / "SHIPPED" / "DELIVERED" / "CANCELED"
}
