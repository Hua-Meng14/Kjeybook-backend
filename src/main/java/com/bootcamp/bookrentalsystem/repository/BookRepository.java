package com.bootcamp.bookrentalsystem.repository;

import com.bootcamp.bookrentalsystem.model.Book;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Qualifier("book")
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
