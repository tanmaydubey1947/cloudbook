package com.cloudbook.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogRequest {

    private String title;
    private String author;
    private String genre;
    private double price;
    private int stock;
    private double rating;

}
