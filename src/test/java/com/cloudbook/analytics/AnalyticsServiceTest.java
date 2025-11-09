package com.cloudbook.analytics;

import com.cloudbook.analytics.dto.SalesSummaryResponse;
import com.cloudbook.analytics.dto.TopBookResponse;
import com.cloudbook.analytics.service.AnalyticsService;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.order.model.Order;
import com.cloudbook.order.model.OrderItem;
import com.cloudbook.order.model.OrderStatus;
import com.cloudbook.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTopBooks() {
        UUID book1 = UUID.randomUUID();
        UUID book2 = UUID.randomUUID();

        Book b1 = new Book();
        b1.setId(book1);
        Book b2 = new Book();
        b2.setId(book2);

        OrderItem item1 = new OrderItem();
        item1.setBook(b1);
        item1.setQuantity(3);

        OrderItem item2 = new OrderItem();
        item2.setBook(b2);
        item2.setQuantity(5);

        Order order1 = new Order();
        order1.setStatus(OrderStatus.PLACED);
        order1.setItems(List.of(item1, item2));

        Order order2 = new Order();
        order2.setStatus(OrderStatus.FULFILLED);
        order2.setItems(List.of(item1));

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<TopBookResponse> result = analyticsService.getTopBooks();

        assertEquals(2, result.size());
        assertEquals(book2, result.get(1).getBookId());
        assertEquals(5L, result.get(1).getTotalSold());
    }

    @Test
    void testGetSalesSummary() {
        Book b1 = new Book();
        b1.setId(UUID.randomUUID());

        Book b2 = new Book();
        b2.setId(UUID.randomUUID());

        OrderItem item1 = new OrderItem();
        item1.setBook(b1);
        item1.setQuantity(2);
        item1.setPriceAtPurchase(new BigDecimal("100"));

        OrderItem item2 = new OrderItem();
        item2.setBook(b2);
        item2.setQuantity(1);
        item2.setPriceAtPurchase(new BigDecimal("200"));

        Order order = new Order();
        order.setStatus(OrderStatus.PLACED);
        order.setItems(List.of(item1, item2));
        order.setTotalAmount(new BigDecimal("400"));
        order.setOrderDate(LocalDateTime.now());

        when(orderRepository.findAll()).thenReturn(List.of(order));

        SalesSummaryResponse response = analyticsService.getSalesSummary();

        assertEquals(new BigDecimal("400"), response.getTotalRevenue());
        assertEquals(1, response.getDailyBreakdown().size());
        assertTrue(response.getDailyBreakdown().containsKey(LocalDate.now()));
    }

}
