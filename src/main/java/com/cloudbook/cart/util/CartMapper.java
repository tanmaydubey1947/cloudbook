package com.cloudbook.cart.util;

import com.cloudbook.cart.dto.CartItemResponse;
import com.cloudbook.cart.dto.CartResponse;
import com.cloudbook.cart.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setItems(cart.getItems().stream().map(item -> {
            CartItemResponse i = new CartItemResponse();
            i.setBookId(item.getBook().getId());
            i.setTitle(item.getBook().getTitle());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getBook().getPrice());
            return i;
        }).collect(Collectors.toList()));

        BigDecimal total = response.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotal(total);
        return response;
    }
}
