package com.cloudbook.order.dto;

import com.cloudbook.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse extends BaseResponse {

    private UUID id;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;

    @Data
    @AllArgsConstructor
    public static class Item {
        private UUID bookId;
        private String title;
        private int quantity;
        private BigDecimal priceAtPurchase;
    }

}
