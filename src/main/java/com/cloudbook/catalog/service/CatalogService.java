package com.cloudbook.catalog.service;

import com.cloudbook.catalog.dto.CatalogRequest;
import com.cloudbook.catalog.dto.CatalogResponse;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.catalog.repository.CatalogRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;


    public Page<CatalogResponse> listBooks(String genre,
                                           String author,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice,
                                           int page,
                                           int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());

        List<Book> allBooks = catalogRepository.findAll();
        List<Book> filtered = new ArrayList<>();
        log.info("Filtering book details with filters - genre: {}, author: {}, minPrice: {}, maxPrice: {}, page: {}, size: {}",
                genre, author, minPrice, maxPrice, page, size);

        for (Book book : allBooks) {
            boolean matches = genre == null || genre.isBlank() || book.getGenre().equalsIgnoreCase(genre);

            if (author != null && !author.isBlank() && !book.getAuthor().equalsIgnoreCase(author)) {
                matches = false;
            }
            if (minPrice != null && book.getPrice().compareTo(minPrice) < 0) {
                matches = false;
            }
            if (maxPrice != null && book.getPrice().compareTo(maxPrice) > 0) {
                matches = false;
            }

            if (matches) filtered.add(book);
        }

        int start = Math.min((int) pageable.getOffset(), filtered.size());
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Book> pagedBooks = filtered.subList(start, end);

        List<CatalogResponse> bookResponses = pagedBooks.stream()
                .map(book -> new CatalogResponse(
                        book.getId().toString(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPrice()
                ))
                .toList();

        return new PageImpl<>(bookResponses, pageable, filtered.size());
    }

    public CatalogResponse getBookById(String bookId) {
        Book book = catalogRepository.findById(UUID.fromString(bookId))
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        return new CatalogResponse(
                book.getId().toString(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice()
        );
    }

    public CatalogResponse addBook(CatalogRequest request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .genre(request.getGenre())
                .price(request.getPrice())
                .stock(request.getStock())
                .rating(request.getRating())
                .version(1L)
                .build();

        Book savedBook = catalogRepository.save(book);
        return new CatalogResponse(
                savedBook.getId().toString(),
                savedBook.getTitle(),
                savedBook.getAuthor(),
                savedBook.getPrice()
        );
    }

    public CatalogResponse updateBook(String bookId, CatalogRequest request) {
        Book existingBook = catalogRepository.findById(UUID.fromString(bookId))
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setGenre(request.getGenre());
        existingBook.setPrice(request.getPrice());
        existingBook.setStock(request.getStock());
        existingBook.setRating(request.getRating());
        existingBook.setVersion(existingBook.getVersion() + 1);

        Book updatedBook = catalogRepository.save(existingBook);
        return new CatalogResponse(
                updatedBook.getId().toString(),
                updatedBook.getTitle(),
                updatedBook.getAuthor(),
                updatedBook.getPrice()
        );
    }

    public CatalogResponse deleteBook(String bookId) {
        Book existingBook = catalogRepository.findById(UUID.fromString(bookId))
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        catalogRepository.delete(existingBook);
        CatalogResponse response = new CatalogResponse();
        response.setMessage("Book deleted successfully");
        return response;
    }

    @Transactional
    @Retry(name = "stockUpdateRetry", fallbackMethod = "handleStockUpdateFailure")
    public CatalogResponse updateStock(String bookId, int delta) {
        Book existingBook = catalogRepository.findById(UUID.fromString(bookId))
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        int newStock = existingBook.getStock() + delta;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock for book id: " + bookId);
        }
        existingBook.setStock(newStock);
        Book updatedBook;
        try {
            updatedBook = catalogRepository.save(existingBook);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Concurrent stock update detected for book: " + bookId);
        }
        return new CatalogResponse(
                updatedBook.getId().toString(),
                updatedBook.getTitle(),
                updatedBook.getAuthor(),
                updatedBook.getPrice()
        );
    }

    public CatalogResponse handleStockUpdateFailure(String bookId, Throwable ex) {
        log.error("Stock update failed for book " + bookId + ": " + ex.getMessage());
        throw new RuntimeException("Could not update stock after retries. Please try again later.");
    }
}
