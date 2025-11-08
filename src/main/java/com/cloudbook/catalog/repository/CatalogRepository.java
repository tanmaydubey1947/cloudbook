package com.cloudbook.catalog.repository;

import com.cloudbook.catalog.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatalogRepository extends JpaRepository<Book, UUID> {
}
