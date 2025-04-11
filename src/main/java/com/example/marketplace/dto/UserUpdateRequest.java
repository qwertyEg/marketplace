package com.example.marketplace.dto;

public record UserUpdateRequest(
        String address,
        String phone
) {}