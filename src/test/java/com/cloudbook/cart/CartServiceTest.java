package com.cloudbook.cart;

import com.cloudbook.auth.model.User;
import com.cloudbook.auth.service.auth.AuthUserDetails;
import com.cloudbook.cart.dto.CartRequest;
import com.cloudbook.cart.dto.CartResponse;
import com.cloudbook.cart.model.Cart;
import com.cloudbook.cart.model.CartItem;
import com.cloudbook.cart.repository.CartRepository;
import com.cloudbook.cart.service.CartService;
import com.cloudbook.cart.util.CartMapper;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.catalog.repository.CatalogRepository;
import com.cloudbook.common.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CatalogRepository catalogRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private Book book;
    private CartResponse response;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cart = new Cart("testuser");
        book = new Book();
        book.setId(UUID.randomUUID());
        response = new CartResponse();
        response.setMessage("ok");

        // Mock Security Context
        AuthUserDetails userDetails = new AuthUserDetails(new User(UUID.randomUUID(), "username","password", Role.ADMIN, LocalDateTime.now()));
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetCurrentUserCart_NewCartCreated() {
        when(cartRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(response);

        CartResponse result = cartService.getCurrentUserCart();

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddToCart_NewItem() {
        CartRequest req = new CartRequest();
        req.setBookId(book.getId());
        req.setQuantity(2);

        when(cartRepository.findByUsername("testuser")).thenReturn(Optional.of(cart));
        when(catalogRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(response);

        CartResponse result = cartService.addToCart(req);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testRemoveItem() {
        CartItem item = new CartItem(book, 1, cart);
        cart.getItems().add(item);

        when(cartRepository.findByUsername("username")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        when(cartMapper.toResponse(any())).thenReturn(response);

        CartResponse result = cartService.removeItem(book.getId().toString());

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testClearCart() {
        cart.getItems().add(new CartItem(book, 1, cart));
        when(cartRepository.findByUsername("username")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(response);

        CartResponse result = cartService.clearCart();

        assertNotNull(result);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testAddToCart_BookNotFound() {
        CartRequest req = new CartRequest();
        req.setBookId(UUID.randomUUID());
        req.setQuantity(1);

        when(cartRepository.findByUsername("testuser")).thenReturn(Optional.of(cart));
        when(catalogRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.addToCart(req));
    }

    @Test
    void testAddToCart_InvalidQuantity() {
        CartRequest req = new CartRequest();
        req.setBookId(book.getId());
        req.setQuantity(0);

        when(cartRepository.findByUsername("testuser")).thenReturn(Optional.of(cart));
        when(catalogRepository.findById(book.getId())).thenReturn(Optional.of(book));

        assertThrows(RuntimeException.class, () -> cartService.addToCart(req));
    }
}

