package com.cloudbook.analytics.service;

import com.cloudbook.analytics.dto.SalesSummaryResponse;
import com.cloudbook.analytics.dto.TopBookResponse;
import com.cloudbook.order.model.Order;
import com.cloudbook.order.model.OrderItem;
import com.cloudbook.order.model.OrderStatus;
import com.cloudbook.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    public List<TopBookResponse> getTopBooks() {
        List<Order> allOrders = orderRepository.findAll();

        Map<UUID, Long> bookCountMap = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PLACED || o.getStatus() == OrderStatus.FULFILLED)
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getBook().getId(),
                        Collectors.summingLong(OrderItem::getQuantity)
                ));

        return bookCountMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new TopBookResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public SalesSummaryResponse getSalesSummary() {
        List<Order> orders = orderRepository.findAll();

        Map<LocalDate, BigDecimal> dailySales = new HashMap<>();

        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.FULFILLED) {
                LocalDate date = order.getOrderDate().atZone(ZoneOffset.UTC).toLocalDate();
                dailySales.merge(date, order.getTotalAmount(), BigDecimal::add);
            }
        }

        BigDecimal total = dailySales.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesSummaryResponse.builder()
                .totalRevenue(total)
                .dailyBreakdown(dailySales)
                .build();
    }
}
