package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public BookService(@Qualifier("book") BookRepository bookRepository, UserRepository userRepository,
            RequestRepository requestRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Cacheable(value = "booksByTitle", key = "#title")
    public List<Book> getBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitle(title);

        return books.stream()
                .filter(book -> !book.getDeleted())
                .collect(Collectors.toList());
    }

    @CacheEvict(value = { "books", "booksByTitle", "booksByAuthor" }, allEntries = true)
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Cacheable("books")
    public List<Book> getAllBook() {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .filter(book -> !book.getDeleted())
                .collect(Collectors.toList());
    }

    @CachePut(value = "books", key = "#bookId")
    public Book updateBookById(UUID bookId, Book updatedBook) {
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
        // Optional.ofNullable(updatedBook.getMaximumRequestPeriod())
        // .ifPresent(existingBook::setMaximumRequestPeriod);

        return bookRepository.save(existingBook);
    }

    @CacheEvict(value = { "books", "booksByTitle", "booksByAuthor" }, allEntries = true)
    public String deletBookById(UUID bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Check if the book has any associated requests
        // if (!hasAssociatedRequests(bookId)) {
        // throw new ForeignKeyConstraintException("Cannot delete the book because it
        // has associated requests.");
        // }

        // find users that has associated with book and remove book from the list
        List<User> users = userRepository.findByFavoriteBooks(book);
        boolean isAssociatedWithuser = !users.isEmpty();
        if (isAssociatedWithuser) {
            users.forEach(user -> user.getFavoriteBooks().remove(book));
            userRepository.saveAll(users); // Batch update
        }

        // find requests that has associated with book and check if the associated
        // request has status of 'PENDING' or 'ACCEPTED'
        List<Request> requests = requestRepository.findByBook(book);
        boolean isAssociatedWithRequest = !requests.isEmpty();
        boolean hasPendingOrAcceptedRequests = requests.stream()
                .anyMatch(request -> !request.getStatus().equals("ARCHIVED"));

        if (hasPendingOrAcceptedRequests) {
            throw new BadRequestException("Book is associated with PENDING/ACTIVE request!!");
        }

        book.setDeleted(true);
        bookRepository.save(book);

        if (isAssociatedWithRequest) {
            return "Book has been archived.";
        } else {
            bookRepository.delete(book);
            return "Book has been deleted from the database.";
        }

        // Delete book data if the book has no associate with user and request

    }

    @Cacheable(value = "books", key = "#bookId")
    public Optional<Book> findBookById(UUID bookId) {
        return Optional.ofNullable(bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId)));
    }

    // public boolean hasAssociatedRequests(Long bookId) {
    // Book book = entityManager.find(Book.class, bookId);
    // System.out.println("get all requests relating to the book" +
    // book.getRequests());
    // System.out.println("get request related to the book--------------------" +
    // book.getRequests().isEmpty());
    // return !book.getRequests().isEmpty();
    // }

    @Cacheable(value = "booksByAuthor", key = "#author")
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findAll().stream()
                .filter(book -> !book.getDeleted() && book.getAuthor().contains(author))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "books", key = "#bookId")
    public Book getBookById(UUID bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        return optionalBook.orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
    }
}
