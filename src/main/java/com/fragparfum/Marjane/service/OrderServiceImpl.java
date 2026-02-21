package com.fragparfum.Marjane.service;

import com.fragparfum.Marjane.dto.CreateOrderItemRequest;
import com.fragparfum.Marjane.dto.CreateOrderRequest;
import com.fragparfum.Marjane.exception.BadRequestException;
import com.fragparfum.Marjane.model.*;
import com.fragparfum.Marjane.repository.OrderRepository;
import com.fragparfum.Marjane.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final WhatsAppService whatsAppService;

    @Override
    @Transactional
    public Order create(CreateOrderRequest dto) {

        Order order = new Order();
        order.setCustomerName(dto.getCustomerName());
        order.setPhone(dto.getPhone());
        order.setAddress(dto.getAddress());
        order.setStatus(OrderStatus.PENDING);

        double total = 0.0;

        for (CreateOrderItemRequest itemDto : dto.getItems()) {

            Product product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found: " + itemDto.getProductId()));

            int qty = itemDto.getQuantity() == null ? 0 : itemDto.getQuantity();
            if (qty <= 0) throw new BadRequestException("Quantity must be > 0");

            if (product.getStock() == null || product.getStock() < qty) {
                throw new BadRequestException("Not enough stock for product " + product.getId());
            }

            // نقص stock
            product.setStock(product.getStock() - qty);
            productRepo.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(qty);
            oi.setUnitPrice(product.getPrice());

            double lineTotal = product.getPrice() * qty;
            oi.setLineTotal(lineTotal);

            order.getItems().add(oi);
            total += lineTotal;
        }

        order.setTotal(total);

        Order saved = orderRepo.save(order);

        // ✅ Send WhatsApp to admin using Twilio
        try {
            String message = buildWhatsappMessage(saved.getId());
            whatsAppService.notifyAdmin(message);
        } catch (Exception e) {
            System.out.println("❌ WhatsApp send failed: " + e.getMessage());
        }

        return saved;
    }

    @Override
    public List<Order> getAll() {
        return orderRepo.findAll();
    }
    @Override
    @Transactional
    public void delete(Long id) {
        Order order = getById(id);
        orderRepo.delete(order);
    }


    @Override
    public Order getById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Order not found"));
    }

    @Override
    public Order updateStatus(Long id, OrderStatus newStatus) {

        Order order = getById(id);
        OrderStatus current = order.getStatus();

        if (!isValidTransition(current, newStatus)) {
            throw new BadRequestException("Invalid status transition: " + current + " -> " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepo.save(order);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        if (current == null || next == null) return false;

        return switch (current) {
            case PENDING -> (next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELED);
            case CONFIRMED -> (next == OrderStatus.SHIPPED || next == OrderStatus.CANCELED);
            case SHIPPED -> (next == OrderStatus.DELIVERED);
            case DELIVERED, CANCELED -> false;
        };
    }

    // ✅ This matches your interface method and fixes the override error
    @Override
    public String buildWhatsappMessage(Long orderId) {

        Order order = getById(orderId);

        StringBuilder msg = new StringBuilder();
        msg.append("🛒 Nouvelle commande reçue !\n\n");

        msg.append("Client : ").append(order.getCustomerName()).append("\n");
        msg.append("Téléphone : ").append(order.getPhone()).append("\n");
        msg.append("Adresse : ").append(order.getAddress()).append("\n\n");

        msg.append("Articles:\n");
        order.getItems().forEach(item -> {
            msg.append("- ")
                    .append(item.getProduct().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" = ").append(item.getLineTotal())
                    .append(" DH\n");
        });

        msg.append("\nTotal : ").append(String.format("%.2f", order.getTotal())).append(" DH");
        msg.append("\nCommande N° : ").append(order.getId());

        return msg.toString();
    }
}