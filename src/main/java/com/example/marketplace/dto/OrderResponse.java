package com.example.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String userName,
        LocalDateTime orderDate,
        String status,
        List<OrderItemResponse> items
) {
    public record OrderItemResponse(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal price,
            BigDecimal totalPrice
    ) {}
}