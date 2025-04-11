package com.example.marketplace.controller;

import com.example.marketplace.dto.*;
import com.example.marketplace.model.User;
import com.example.marketplace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(user));
    }

    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(convertToResponse(user));
    }

    @Operation(summary = "Update user address and phone")
    @ApiResponse(responseCode = "200", description = "User updated")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(convertToResponse(user));
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone()
        );
    }
}