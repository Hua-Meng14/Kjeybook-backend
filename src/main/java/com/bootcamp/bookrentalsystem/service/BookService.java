package com.bootcamp.bookrentalsystem.service;


import com.bootcamp.bookrentalsystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(@Qualifier("book") BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

}
