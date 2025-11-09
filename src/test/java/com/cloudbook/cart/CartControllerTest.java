package com.cloudbook.cart;

import com.cloudbook.auth.service.auth.JwtService;
import com.cloudbook.auth.service.auth.filter.JwtAuthFilter;
import com.cloudbook.cart.controller.CartController;
import com.cloudbook.cart.dto.CartRequest;
import com.cloudbook.cart.dto.CartResponse;
import com.cloudbook.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters entirely
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired private ObjectMapper objectMapper;

    @Test
    void testViewCart() throws Exception {
        CartResponse mockResponse = new CartResponse();
        mockResponse.setMessage("Cart fetched successfully");

        Mockito.when(cartService.getCurrentUserCart()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart fetched successfully"));
    }

    @Test
    void testAddToCart() throws Exception {
        CartResponse mockResponse = new CartResponse();
        mockResponse.setMessage("Item added");
        Mockito.when(cartService.addToCart(any())).thenReturn(mockResponse);
        mockMvc.perform(post("/api/cart/add")
                        .content(objectMapper.writeValueAsString(new CartRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Item added"));
    }

    @Test
    void testRemoveFromCart() throws Exception {
        CartResponse mockResponse = new CartResponse();
        mockResponse.setMessage("Item removed");
        Mockito.when(cartService.removeItem(any())).thenReturn(mockResponse);

        mockMvc.perform(delete("/api/cart/remove/1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item removed"));
    }

    @Test
    void testClearCart() throws Exception {
        CartResponse mockResponse = new CartResponse();
        mockResponse.setMessage("Cart cleared");
        Mockito.when(cartService.clearCart()).thenReturn(mockResponse);

        mockMvc.perform(delete("/api/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart cleared"));
    }
}