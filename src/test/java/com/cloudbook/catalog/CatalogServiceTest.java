package com.cloudbook.catalog;

import com.cloudbook.catalog.dto.CatalogRequest;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.catalog.repository.CatalogRepository;
import com.cloudbook.catalog.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CatalogServiceTest {

    @Mock
    private CatalogRepository catalogRepository;

    @InjectMocks
    private CatalogService catalogService;

    private Book book;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        book = Book.builder()
                .id(UUID.randomUUID())
                .title("Book A")
                .author("Author X")
                .genre("Fiction")
                .price(BigDecimal.valueOf(500))
                .stock(10)
                .rating(BigDecimal.valueOf(4.5))
                .version(1L)
                .build();
    }

    @Test
    void testListBooks_filtersAndPagination() {
        when(catalogRepository.findAll()).thenReturn(List.of(book));

        var result = catalogService.listBooks("Fiction", "Author X",
                BigDecimal.valueOf(100), BigDecimal.valueOf(1000), 0, 10);

        assertEquals(1, result.size());
        assertEquals("Book A", result.get(0).getTitle());
        verify(catalogRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_success() {
        when(catalogRepository.findById(any())).thenReturn(Optional.of(book));

        var result = catalogService.getBookById(book.getId().toString());

        assertEquals(book.getTitle(), result.getTitle());
        verify(catalogRepository).findById(any());
    }

    @Test
    void testGetBookById_notFound() {
        when(catalogRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                catalogService.getBookById(UUID.randomUUID().toString()));
    }

    @Test
    void testAddBook_success() {
        when(catalogRepository.save(any())).thenReturn(book);

        CatalogRequest req = new CatalogRequest();
        req.setTitle("Book A");
        req.setAuthor("Author X");
        req.setGenre("Fiction");
        req.setPrice(BigDecimal.valueOf(500));
        req.setStock(10);
        req.setRating(BigDecimal.valueOf(4.5));

        var response = catalogService.addBook(req);

        assertEquals("Book A", response.getTitle());
        verify(catalogRepository).save(any());
    }

    @Test
    void testUpdateBook_success() {
        when(catalogRepository.findById(any())).thenReturn(Optional.of(book));
        when(catalogRepository.save(any())).thenReturn(book);

        CatalogRequest req = new CatalogRequest();
        req.setTitle("Updated");
        req.setAuthor("Author Y");
        req.setGenre("Action");
        req.setPrice(BigDecimal.valueOf(700));
        req.setStock(20);
        req.setRating(BigDecimal.valueOf(5.0));

        var result = catalogService.updateBook(book.getId().toString(), req);

        assertEquals("Updated", result.getTitle());
        verify(catalogRepository).save(any());
    }

    @Test
    void testUpdateBook_notFound() {
        when(catalogRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                catalogService.updateBook(UUID.randomUUID().toString(), new CatalogRequest()));
    }

    @Test
    void testDeleteBook_success() {
        when(catalogRepository.findById(any())).thenReturn(Optional.of(book));

        var result = catalogService.deleteBook(book.getId().toString());

        assertEquals("Book deleted successfully", result.getMessage());
        verify(catalogRepository).delete(any());
    }

    @Test
    void testDeleteBook_notFound() {
        when(catalogRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                catalogService.deleteBook(UUID.randomUUID().toString()));
    }

    @Test
    void testUpdateStock_success() {
        when(catalogRepository.findById(any())).thenReturn(Optional.of(book));
        when(catalogRepository.save(any())).thenReturn(book);

        var result = catalogService.updateStock(book.getId().toString(), 5);

        assertEquals(book.getTitle(), result.getTitle());
        verify(catalogRepository).save(any());
    }

    @Test
    void testUpdateStock_insufficient() {
        when(catalogRepository.findById(any())).thenReturn(Optional.of(book));
        assertThrows(RuntimeException.class, () ->
                catalogService.updateStock(book.getId().toString(), -20));
    }

    @Test
    void testUpdateStock_concurrentFailure() {
        when(catalogRepository.findById(any())).thenReturn(Optional.of(book));
        when(catalogRepository.save(any())).thenThrow(new ObjectOptimisticLockingFailureException(Book.class, book.getId()));

        assertThrows(RuntimeException.class, () ->
                catalogService.updateStock(book.getId().toString(), 1));
    }

    @Test
    void testHandleStockUpdateFailure() {
        RuntimeException ex = new RuntimeException("failure");
        assertThrows(RuntimeException.class, () ->
                catalogService.handleStockUpdateFailure(book.getId().toString(), 1, ex));
    }
}
