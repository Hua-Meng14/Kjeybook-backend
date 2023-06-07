package com.bootcamp.bookrentalsystem.service;


import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Component
@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(@Qualifier("book") BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBookById(Long bookId, Book updatedBook) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        Optional.ofNullable(updatedBook.getTitle())
                .ifPresent(existingBook::setTitle);
        Optional.ofNullable(updatedBook.getAuthor())
                .ifPresent(existingBook::setAuthor);
        Optional.ofNullable(updatedBook.getCategory())
                .ifPresent(existingBook::setCategory);
        Optional.ofNullable(updatedBook.getDescription())
                .ifPresent(existingBook::setDescription);
        Optional.ofNullable(updatedBook.getBookImg())
                .ifPresent(existingBook::setBookImg);
        Optional.ofNullable(updatedBook.getMaximumRequestPeriod())
                .ifPresent(existingBook::setMaximumRequestPeriod);

        return bookRepository.save(existingBook);
    }
}
