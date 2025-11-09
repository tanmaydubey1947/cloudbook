package com.cloudbook.analytics.controller;

import com.cloudbook.analytics.dto.SalesSummaryResponse;
import com.cloudbook.analytics.dto.TopBookResponse;
import com.cloudbook.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Operation(summary = "Get Top Books")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Top books retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping("/top-books")
    public ResponseEntity<List<TopBookResponse>> getTopBooks() {
        return ResponseEntity.ok(analyticsService.getTopBooks());
    }

    @Operation(summary = "Sales Summary")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sales summary retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @GetMapping("/sales-summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary() {
        return ResponseEntity.ok(analyticsService.getSalesSummary());
    }
}
