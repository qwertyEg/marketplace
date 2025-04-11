package com.example.marketplace.controller;

import com.example.marketplace.dto.*;
import com.example.marketplace.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private OrderService orderService;

    @Test
    void createOrder_ShouldReturnCreated() throws Exception {
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderItemRequest(1L, 2)
        ));

        OrderResponse response = new OrderResponse(
                1L, 1L, "User", LocalDateTime.now(), "NEW", List.of(
                new OrderResponse.OrderItemResponse(
                        1L, "Product", 2, BigDecimal.valueOf(100), BigDecimal.valueOf(200))
        ));

        given(orderService.createOrder(any())).willReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType("application/json")
                        .content("""
                    {
                        "userId": 1,
                        "items": [
                            {
                                "productId": 1,
                                "quantity": 2
                            }
                        ]
                    }"""))
                .andExpect(status().isCreated());
    }
}