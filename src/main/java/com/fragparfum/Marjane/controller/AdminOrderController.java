package com.fragparfum.Marjane.controller;

import com.fragparfum.Marjane.dto.OrderResponse;
import com.fragparfum.Marjane.mapper.OrderMapper;
import com.fragparfum.Marjane.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.fragparfum.Marjane.dto.UpdateOrderStatusRequest;
import com.fragparfum.Marjane.model.OrderStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public List<OrderResponse> getAll() {
        return orderService.getAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getOne(@PathVariable Long id) {
        return orderMapper.toResponse(
                orderService.getById(id)
        );
    }
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id,
                                      @RequestBody UpdateOrderStatusRequest dto) {

        OrderStatus status = OrderStatus.valueOf(dto.getStatus().toUpperCase());

        return orderMapper.toResponse(
                orderService.updateStatus(id, status)
        );
    }
    @GetMapping("/{id}/whatsapp")
    public Map<String, String> whatsapp(@PathVariable Long id) {
        String url = orderService.buildWhatsappMessage(id);
        return Map.of("url", url);
    }

}
