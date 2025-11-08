package com.cloudbook.order.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPlacedEventListener {

    @Async
    @EventListener
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Processing OrderPlacedEvent for order ID: {}", event.getOrder().getId());
    }
}
