package com.example.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @Positive(message = "Price must be a positive value")
        BigDecimal price,

        String category
) {}