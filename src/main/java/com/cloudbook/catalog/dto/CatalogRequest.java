package com.cloudbook.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogRequest {

    private String title;
    private String author;
    private String genre;
    private BigDecimal price;
    private int stock;
    private BigDecimal rating;

}
