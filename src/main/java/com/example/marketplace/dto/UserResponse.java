package com.example.marketplace.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String address,
        String phone
) {}