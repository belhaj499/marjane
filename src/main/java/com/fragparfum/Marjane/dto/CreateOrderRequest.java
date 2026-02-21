package com.fragparfum.Marjane.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerName;
    private String phone;
    private String address;
    private List<CreateOrderItemRequest> items;
}
