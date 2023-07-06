package com.bootcamp.bookrentalsystem.service;


import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(@Qualifier("book") BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBook() {
        return bookRepository.findAll();
    }

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
        //         .ifPresent(existingBook::setMaximumRequestPeriod);

        return bookRepository.save(existingBook);
    }

    public Map<String, Boolean> deletBookById(UUID bookId) {
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

//         Check if the book has any associated requests
//        if (!hasAssociatedRequests(bookId)) {
//            throw new ForeignKeyConstraintException("Cannot delete the book because it has associated requests.");
//        }

        List<User> users = userRepository.findByFavoriteBooks(book);
        for(User user: users) {
            user.getFavoriteBooks().remove(book);
            userRepository.save(user);
        }


        bookRepository.deleteById(bookId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    public Optional<Book> findBookById(UUID bookId) {
        return Optional.ofNullable(bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId)
                ));
    }

//    public boolean hasAssociatedRequests(Long bookId) {
//        Book book = entityManager.find(Book.class, bookId);
//        System.out.println("get all requests relating to the book" + book.getRequests());
//        System.out.println("get request related to the book--------------------" + book.getRequests().isEmpty());
//        return !book.getRequests().isEmpty();
//    }

    public List<Book> getBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();

        for (Book book : bookRepository.findAll()) {
            if (book.getAuthor().contains(author)) {
                result.add(book);
            }
        }
        return result;
    }

    public Book getBookById(UUID bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        return optionalBook.orElse(null);
    }
}
