package com.cloudbook.order.util;

import com.cloudbook.order.dto.OrderItemResponse;
import com.cloudbook.order.dto.OrderResponse;
import com.cloudbook.order.model.Order;
import com.cloudbook.order.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .items(order.getItems().stream().map(item -> OrderItemResponse.builder()
                        .bookId(item.getBook().getId())
                        .title(item.getBook().getTitle())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .build()).toList())
                .build();
    }

    public List<OrderItemResponse> toItemResponseList(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }
    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .bookId(item.getBook().getId())
                .title(item.getBook().getTitle())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getPriceAtPurchase())
                .build();
    }
}
