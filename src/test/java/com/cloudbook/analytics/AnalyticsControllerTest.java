package com.cloudbook.analytics;

import com.cloudbook.analytics.controller.AnalyticsController;
import com.cloudbook.analytics.dto.SalesSummaryResponse;
import com.cloudbook.analytics.dto.TopBookResponse;
import com.cloudbook.analytics.service.AnalyticsService;
import com.cloudbook.auth.service.auth.JwtService;
import com.cloudbook.auth.service.auth.filter.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters entirely
class AnalyticsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void testGetTopBooks() throws Exception {
        when(analyticsService.getTopBooks()).thenReturn(List.of(
                new TopBookResponse(UUID.randomUUID(), 100L),
                new TopBookResponse(UUID.randomUUID(), 80L)
        ));

        mockMvc.perform(get("/api/admin/analytics/top-books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(analyticsService).getTopBooks();
    }

    @Test
    void testGetSalesSummary() throws Exception {
        Map<LocalDate, BigDecimal> daily = Map.of(
                LocalDate.of(2024, 1, 1), BigDecimal.valueOf(200),
                LocalDate.of(2024, 1, 2), BigDecimal.valueOf(300)
        );
        SalesSummaryResponse summary = new SalesSummaryResponse(BigDecimal.valueOf(500), daily);
        when(analyticsService.getSalesSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/admin/analytics/sales-summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(analyticsService).getSalesSummary();
    }
}