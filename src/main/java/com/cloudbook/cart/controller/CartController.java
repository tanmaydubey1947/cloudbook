package com.cloudbook.cart.controller;

import com.cloudbook.cart.dto.CartRequest;
import com.cloudbook.cart.service.CartService;
import com.cloudbook.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "View Cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> viewCart() {
        final BaseResponse response = cartService.getCurrentUserCart();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Add to Cart")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item added to cart successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> addToCart(@RequestBody CartRequest request) {
        final BaseResponse response = cartService.addToCart(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Remove an item from Cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed from cart successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @DeleteMapping("/remove/{bookId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> removeFromCart(@PathVariable("bookId") String bookId) {
        final BaseResponse response = cartService.removeItem(bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Clear Cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> clearCart() {
        final BaseResponse response = cartService.clearCart();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
