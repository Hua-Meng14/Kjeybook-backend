package com.bootcamp.bookrentalsystem.service;


import com.bootcamp.bookrentalsystem.exception.ForeignKeyConstraintException;
import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.util.*;
=======
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
>>>>>>> f66eadec7973ea5f6b7942b6a0bc53cf5978e1b5

@Component
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final EntityManager entityManager;

    @Autowired
    public BookService(@Qualifier("book") BookRepository bookRepository, EntityManager entityManager) {
        this.bookRepository = bookRepository;
        this.entityManager = entityManager;
    }

    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBook(){
        return bookRepository.findAll();
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

    public Map<String, Boolean> deletBookById(Long bookId) {
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        System.out.println("-----------------------------------"+hasAssociatedRequests(bookId));

//         Check if the book has any associated requests
        if (!hasAssociatedRequests(bookId)) {
            throw new ForeignKeyConstraintException("Cannot delete the book because it has associated requests.");
        }

        bookRepository.deleteById(bookId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    public Optional<Object> findBookById(Long bookId) {
        return Optional.ofNullable(bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId)
                ));
    }

    public boolean hasAssociatedRequests(Long bookId) {
        Book book = entityManager.find(Book.class, bookId);
        System.out.println("get all requests relating to the book"+ book.getRequests());
        System.out.println("get request related to the book--------------------"+book.getRequests().isEmpty());
        return !book.getRequests().isEmpty();
    }

}
