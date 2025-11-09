package com.cloudbook.order;

import com.cloudbook.auth.model.User;
import com.cloudbook.auth.repository.UserRepository;
import com.cloudbook.cart.model.Cart;
import com.cloudbook.cart.model.CartItem;
import com.cloudbook.cart.repository.CartRepository;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.catalog.repository.CatalogRepository;
import com.cloudbook.common.enums.Role;
import com.cloudbook.order.dto.OrderResponse;
import com.cloudbook.order.repository.OrderRepository;
import com.cloudbook.order.service.OrderService;
import com.cloudbook.order.util.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CatalogRepository catalogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john", null, List.of())
        );
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(new User(UUID.randomUUID(), "john", "password", Role.CUSTOMER, LocalDateTime.now())));
    }

    @Test
    void testPlaceOrder_success() {
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title("Test Book")
                .price(BigDecimal.TEN)
                .stock(5)
                .build();

        CartItem item = new CartItem(book, 2, null);
        Cart cart = new Cart("john");
        cart.getItems().add(item);

        when(cartRepository.findByUsername("john")).thenReturn(Optional.of(cart));
        when(catalogRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        OrderResponse mockResponse = new OrderResponse();
        when(orderMapper.toResponse(any())).thenReturn(mockResponse);

        OrderResponse response = orderService.placeOrder();

        assertNotNull(response);
        verify(orderRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
