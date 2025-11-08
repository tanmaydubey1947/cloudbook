package com.cloudbook.catalog.dto;

import com.cloudbook.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogResponse extends BaseResponse {

    private String bookId;
    private String title;
    private String author;
    private BigDecimal price;
}
