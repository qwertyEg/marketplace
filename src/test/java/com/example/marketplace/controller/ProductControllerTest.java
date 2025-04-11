package com.example.marketplace.controller;

import com.example.marketplace.dto.ProductRequest;
import com.example.marketplace.model.Product;
import com.example.marketplace.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetProducts() throws Exception {
        when(productService.getAllProducts("electronics", new BigDecimal("100"), new BigDecimal("500")))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products")
                        .param("category", "electronics")
                        .param("min_price", "100")
                        .param("max_price", "500"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateProduct_Success() throws Exception {
        ProductRequest request = new ProductRequest(
                "Test Product",
                "Описание",
                new BigDecimal("99.99"),
                "Категория"
        );

        Product mockProduct = new Product();
        mockProduct.setName(request.name());
        mockProduct.setDescription(request.description());
        mockProduct.setPrice(request.price());
        mockProduct.setCategory(request.category());

        when(productService.createProduct(any(Product.class))).thenReturn(mockProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    public void testCreateProduct_ValidationFailed() throws Exception {
        ProductRequest invalidRequest = new ProductRequest(
                "",
                "Описание",
                new BigDecimal("-100"),
                "Категория"
        );

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_Success() throws Exception {
        ProductRequest request = new ProductRequest(
                "Updated Product",
                "New Description",
                new BigDecimal("199.99"),
                "New Category"
        );

        Product updatedProduct = new Product();
        updatedProduct.setName(request.name());
        updatedProduct.setDescription(request.description());
        updatedProduct.setPrice(request.price());
        updatedProduct.setCategory(request.category());

        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenReturn(Optional.of(updatedProduct));

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    void updateProduct_NotFound() throws Exception {
        ProductRequest request = new ProductRequest(
                "Test Product",
                "Описание",
                new BigDecimal("99.99"),
                "Категория"
        );

        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }
}