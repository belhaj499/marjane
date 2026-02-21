package com.fragparfum.Marjane.service;

import com.fragparfum.Marjane.dto.CreateOrderRequest;
import com.fragparfum.Marjane.model.Order;
import com.fragparfum.Marjane.model.Order;
import com.fragparfum.Marjane.model.OrderStatus;
import java.util.List;

public interface OrderService {
    Order create(CreateOrderRequest dto);
    List<Order> getAll();
    Order getById(Long id);
    Order updateStatus(Long id, OrderStatus status);
    String buildWhatsappMessage(Long orderId);
    void delete(Long id);

}
