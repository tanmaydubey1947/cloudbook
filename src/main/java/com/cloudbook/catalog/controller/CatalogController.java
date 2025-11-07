package com.cloudbook.catalog.controller;

import com.cloudbook.catalog.dto.CatalogRequest;
import com.cloudbook.catalog.dto.CatalogResponse;
import com.cloudbook.catalog.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class CatalogController {


    @Autowired
    private CatalogService catalogService;

    @Operation(summary = "Get All Book Details With Pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book details fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity<Page<CatalogResponse>> listBooks(@RequestParam(required = false) String genre,
                                                  @RequestParam(required = false) String author,
                                                  @RequestParam(required = false) BigDecimal minPrice,
                                                  @RequestParam(required = false) BigDecimal maxPrice,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching book details with filters...");
        Page<CatalogResponse> response = catalogService.listBooks(genre, author, minPrice, maxPrice, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get Book By ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity<CatalogResponse> getBookById(@PathVariable String bookId) {
        log.info("Fetching book details for ID: {}", bookId);
        CatalogResponse response = catalogService.getBookById(bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Add a New Book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CatalogResponse> addBook(@RequestBody CatalogRequest request) {
        log.info("Adding a new book...");
        CatalogResponse response = catalogService.addBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Book Details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CatalogResponse> updateBook(@PathVariable String bookId,
                                                      @RequestBody CatalogRequest request) {
        log.info("Updating book details for ID: {}", bookId);
        CatalogResponse response = catalogService.updateBook(bookId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete Book By ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CatalogResponse> deleteBook(@PathVariable String bookId) {
        log.info("Deleting book with ID: {}", bookId);
        CatalogResponse response = catalogService.deleteBook(bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update Book Stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PatchMapping("/{bookId}/stock")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CatalogResponse> updateStock(@PathVariable String bookId, @RequestParam int newStock) {
        log.info("Updating stock for book ID: {}", bookId);
        CatalogResponse response = catalogService.updateStock(bookId, newStock);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
