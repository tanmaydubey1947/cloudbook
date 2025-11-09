package com.cloudbook.analytics.dto;

import com.cloudbook.common.dto.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesSummaryResponse extends BaseResponse {
    private BigDecimal totalRevenue;
    private Map<LocalDate, BigDecimal> dailyBreakdown;
}
