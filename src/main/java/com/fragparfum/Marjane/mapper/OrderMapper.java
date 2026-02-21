package com.fragparfum.Marjane.mapper;

import com.fragparfum.Marjane.dto.OrderItemResponse;
import com.fragparfum.Marjane.dto.OrderResponse;
import com.fragparfum.Marjane.model.Order;
import com.fragparfum.Marjane.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setCustomerName(o.getCustomerName());
        r.setPhone(o.getPhone());
        r.setAddress(o.getAddress());
        r.setStatus(o.getStatus().name());
        r.setTotal(o.getTotal());
        r.setCreatedAt(o.getCreatedAt());

        r.setItems(
                o.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList())
        );

        return r;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse ir = new OrderItemResponse();
        ir.setId(item.getId());
        ir.setProductId(item.getProduct().getId());
        ir.setProductName(item.getProduct().getName());
        ir.setUnitPrice(item.getUnitPrice());
        ir.setQuantity(item.getQuantity());
        ir.setLineTotal(item.getLineTotal());
        ir.setImageUrl(item.getProduct().getImageUrl());
        return ir;
    }
}
