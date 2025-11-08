package com.cloudbook.cart.dto;

import com.cloudbook.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse extends BaseResponse {

    private List<CartItemResponse> items;
    private BigDecimal total;
}
