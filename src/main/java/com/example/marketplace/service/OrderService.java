package com.example.marketplace.service;

import com.example.marketplace.dto.*;
import com.example.marketplace.exception.*;
import com.example.marketplace.model.*;
import com.example.marketplace.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.marketplace.exception.InvalidQuantityException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.NEW);

        for (OrderItemRequest item : request.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));

            if (item.quantity() <= 0) {
                throw new InvalidQuantityException("Quantity must be positive");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.quantity());
            orderItem.setPrice(product.getPrice());

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return convertToResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() == Order.Status.COMPLETED) {
            throw new IllegalStateException("Cannot update status of a completed order");
        }

        try {
            Order.Status newStatus = Order.Status.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            return convertToResponse(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException("Invalid status: " + status);
        }
    }

    private OrderResponse convertToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getOrderDate(),
                order.getStatus().name(),
                order.getItems().stream()
                        .map(this::convertItemToResponse)
                        .collect(Collectors.toList())
        );
    }

    private OrderResponse.OrderItemResponse convertItemToResponse(OrderItem item) {
        return new OrderResponse.OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}