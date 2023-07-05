package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.Book;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Component
@Qualifier("book")
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByTitle(String title);
}
