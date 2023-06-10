package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;

    @Autowired
    public BookController(BookService bookService, UserService userService, RequestService requestService) {
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
    }

    @GetMapping
    public List<Book> getAllBooks(){
        return bookService.getAllBook();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return this.bookService.createBook(book);
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @RequestBody Book updatedBook) {
        Book book = this.bookService.updateBookById(bookId, updatedBook);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, Boolean>> deleteBookB(@PathVariable Long bookId) {
        Map<String, Boolean> response = bookService.deletBookById(bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
