package com.example.marketplace.controller;

import com.example.marketplace.dto.ProductResponse;
import com.example.marketplace.dto.RatingRequest;
import com.example.marketplace.model.Product;
import com.example.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductRatingTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ProductService productService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void rateProduct_ValidRating_ReturnsUpdatedProduct() throws Exception {
        Product product = new Product();
        product.setAverageRating(new BigDecimal("4.50"));

        when(productService.rateProduct(eq(1L), eq(123L), eq(4)))
                .thenReturn(product);

        mockMvc.perform(post("/products/1/rate")
                        .header("X-User-Id", 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RatingRequest(4))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.50));
    }

    @Test
    void rateProduct_InvalidRating_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/products/1/rate")
                        .header("X-User-Id", 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating must be at least 1"));
    }

    @Test
    void getProduct_WithRating_ReturnsAverage() throws Exception {
        Product product = new Product();
        product.setAverageRating(new BigDecimal("4.75"));

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.75));
    }
}