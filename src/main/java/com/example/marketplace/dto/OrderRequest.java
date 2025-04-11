package com.example.marketplace.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Items cannot be empty")
        List<OrderItemRequest> items
) {}