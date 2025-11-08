package com.cloudbook.order.dto;

import com.cloudbook.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse extends BaseResponse {
    private UUID bookId;
    private String title;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
