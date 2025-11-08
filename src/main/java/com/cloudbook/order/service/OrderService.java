package com.cloudbook.order.service;

import com.cloudbook.auth.model.User;
import com.cloudbook.auth.repository.UserRepository;
import com.cloudbook.cart.model.Cart;
import com.cloudbook.cart.repository.CartRepository;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.catalog.repository.CatalogRepository;
import com.cloudbook.order.dto.OrderResponse;
import com.cloudbook.order.model.Order;
import com.cloudbook.order.model.OrderItem;
import com.cloudbook.order.model.OrderStatus;
import com.cloudbook.order.repository.OrderRepository;
import com.cloudbook.order.util.OrderMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder() {
        String username = getCurrentUsername();
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUsername(username);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        BigDecimal total = BigDecimal.ZERO;

        for (var item : cart.getItems()) {
            Book book = catalogRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            if (book.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
            }

            book.setStock(book.getStock() - item.getQuantity());
            catalogRepository.save(book);

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(book.getPrice());
            orderItem.setOrder(order);

            order.getItems().add(orderItem);
            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse getUserOrders() { //TODO: Might need to change logic
        String username = getCurrentUsername();
        List<Order> ordersByUser = orderRepository.findByUsername(username);
        return OrderResponse.builder()
                .items(orderMapper.toItemResponseList(ordersByUser))
                .build();
    }

    public OrderResponse getOrderById(String orderId) {
        String username = getCurrentUsername();
        UUID orderUUID = UUID.fromString(orderId);
        Order order = orderRepository.findById(orderUUID)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        return orderMapper.toResponse(order);
    }

    @Retry(name = "stockUpdateRetry", fallbackMethod = "handleCancelOrderFailure")
    @Transactional
    public void cancelOrder(String orderId) {
        String username = getCurrentUsername();
        UUID orderUUID = UUID.fromString(orderId);
        Order order = orderRepository.findById(orderUUID)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException("Cannot cancel this order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Restock books with optimistic locking
        for (OrderItem item : order.getItems()) {
            Book book = catalogRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            book.setStock(book.getStock() + item.getQuantity());
            catalogRepository.save(book);
        }

        orderRepository.save(order);
    }

    public void handleCancelOrderFailure(String orderId, Throwable ex) {
        log.error("Failed to cancel order " + orderId + ": " + ex.getMessage());
        throw new RuntimeException("Could not cancel order due to concurrent stock updates. Please try again.");
    }


    private String getCurrentUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .map(User::getUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

