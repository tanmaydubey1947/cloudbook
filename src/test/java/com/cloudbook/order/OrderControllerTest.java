package com.cloudbook.order;

import com.cloudbook.auth.service.auth.JwtService;
import com.cloudbook.auth.service.auth.filter.JwtAuthFilter;
import com.cloudbook.order.controller.OrderController;
import com.cloudbook.order.dto.OrderResponse;
import com.cloudbook.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;



    @Test
    void testCreateOrder_returns201() throws Exception {
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setMessage("Order created");

        when(orderService.placeOrder()).thenReturn(mockResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Order created"));

        verify(orderService).placeOrder();
    }

    @Test
    void testViewOrderById_returns200() throws Exception {
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setMessage("Order details");

        when(orderService.getOrderById("123")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/orders/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order details"));

        verify(orderService).getOrderById("123");
    }

    @Test
    void testViewUserOrders_returns200() throws Exception {
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setMessage("User orders");

        when(orderService.getUserOrders()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User orders"));

        verify(orderService).getUserOrders();
    }

    @Test
    void testCancelOrder_returns204() throws Exception {
        Mockito.doNothing().when(orderService).cancelOrder("123");

        mockMvc.perform(patch("/api/orders/123/cancel"))
                .andExpect(status().isNoContent());

        verify(orderService).cancelOrder("123");
    }
}
