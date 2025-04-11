package com.example.marketplace.controller;

import com.example.marketplace.dto.UserRequest;
import com.example.marketplace.dto.UserResponse;
import com.example.marketplace.dto.UserUpdateRequest;
import com.example.marketplace.exception.DuplicateEmailException;
import com.example.marketplace.exception.UserNotFoundException;
import com.example.marketplace.model.User;
import com.example.marketplace.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_Success() throws Exception {
        UserRequest request = new UserRequest("John Doe", "john@example.com", "password123");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName(request.name());
        mockUser.setEmail(request.email());

        when(userService.registerUser(any(UserRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void registerUser_DuplicateEmail() throws Exception {
        UserRequest request = new UserRequest("John Doe", "john@example.com", "password123");

        when(userService.registerUser(any(UserRequest.class)))
                .thenThrow(new DuplicateEmailException("Email already registered"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void getUserById_Success() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setAddress("123 Main St");
        mockUser.setPhone("555-1234");

        when(userService.getUserById(anyLong())).thenReturn(mockUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.phone").value("555-1234"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("456 Oak Ave", "555-5678");
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setAddress(updateRequest.address());
        updatedUser.setPhone(updateRequest.phone());

        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("456 Oak Ave"))
                .andExpect(jsonPath("$.phone").value("555-5678"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        UserUpdateRequest updateRequest = new UserUpdateRequest("456 Oak Ave", "555-5678");

        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}