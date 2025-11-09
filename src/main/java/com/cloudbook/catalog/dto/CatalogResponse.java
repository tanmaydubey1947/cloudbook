package com.cloudbook.catalog.dto;

import com.cloudbook.common.dto.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogResponse extends BaseResponse {

    private String bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private String genre;
}
