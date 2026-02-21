package com.fragparfum.Marjane.controller;

import com.fragparfum.Marjane.dto.CreateOrderRequest;
import com.fragparfum.Marjane.dto.OrderResponse;
import com.fragparfum.Marjane.mapper.OrderMapper;
import com.fragparfum.Marjane.model.OrderStatus;
import com.fragparfum.Marjane.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public OrderResponse create(@RequestBody CreateOrderRequest dto) {
        return orderMapper.toResponse(orderService.create(dto));
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return orderService.getAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderMapper.toResponse(orderService.getById(id));
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }


    // ✅ ADD THIS
    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return orderMapper.toResponse(orderService.updateStatus(id, status));
    }
}