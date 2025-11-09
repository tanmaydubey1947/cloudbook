package com.cloudbook.catalog;

import com.cloudbook.auth.service.auth.JwtService;
import com.cloudbook.auth.service.auth.filter.JwtAuthFilter;
import com.cloudbook.catalog.controller.CatalogController;
import com.cloudbook.catalog.dto.CatalogRequest;
import com.cloudbook.catalog.dto.CatalogResponse;
import com.cloudbook.catalog.service.CatalogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security filters
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListBooks() throws Exception {
        CatalogResponse res = new CatalogResponse();
        res.setTitle("Book 1");

        Mockito.when(catalogService.listBooks(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(res));

        mockMvc.perform(get("/api/books")
                        .param("genre", "Fiction")
                        .param("author", "Author")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book 1"));
    }

    @Test
    void testGetBookById() throws Exception {
        String bookId = UUID.randomUUID().toString();
        CatalogResponse res = new CatalogResponse();
        res.setTitle("Book Title");

        Mockito.when(catalogService.getBookById(bookId)).thenReturn(res);

        mockMvc.perform(get("/api/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void testAddBook() throws Exception {
        CatalogRequest req = new CatalogRequest();
        req.setTitle("New Book");
        req.setPrice(BigDecimal.TEN);

        CatalogResponse res = new CatalogResponse();
        res.setTitle("New Book");

        Mockito.when(catalogService.addBook(any(CatalogRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Book"));
    }

    @Test
    void testUpdateBook() throws Exception {
        String bookId = UUID.randomUUID().toString();
        CatalogRequest req = new CatalogRequest();
        req.setTitle("Updated Book");

        CatalogResponse res = new CatalogResponse();
        res.setTitle("Updated Book");

        Mockito.when(catalogService.updateBook(eq(bookId), any(CatalogRequest.class)))
                .thenReturn(res);

        mockMvc.perform(put("/api/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void testDeleteBook() throws Exception {
        String bookId = UUID.randomUUID().toString();
        CatalogResponse res = new CatalogResponse();
        res.setMessage("Book deleted");

        Mockito.when(catalogService.deleteBook(bookId)).thenReturn(res);

        mockMvc.perform(delete("/api/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted"));
    }

    @Test
    void testUpdateStock() throws Exception {
        String bookId = UUID.randomUUID().toString();
        CatalogRequest req = new CatalogRequest();
        req.setDelta(5);

        CatalogResponse res = new CatalogResponse();
        res.setMessage("Stock updated");

        Mockito.when(catalogService.updateStock(bookId, 5)).thenReturn(res);

        mockMvc.perform(patch("/api/books/" + bookId + "/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stock updated"));
    }
}