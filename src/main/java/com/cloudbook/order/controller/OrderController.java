package com.cloudbook.order.controller;

import com.cloudbook.common.dto.BaseResponse;
import com.cloudbook.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Create Order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> createOrder() {
        final BaseResponse response = orderService.placeOrder();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "View Order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping("{orderId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> viewOrder(@PathVariable("orderId") String orderId) {
        final BaseResponse response = orderService.getOrderById(orderId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "View User Order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<BaseResponse> viewUserOrder() {
        final BaseResponse response = orderService.getUserOrders();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel Order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @PatchMapping("{orderId}/cancel")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> cancelOrder(@PathVariable("orderId") String orderId) {
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
