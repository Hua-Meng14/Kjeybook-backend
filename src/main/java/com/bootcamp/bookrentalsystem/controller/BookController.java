package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.JwtService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;
    private final JwtService jwtService;

    @Autowired
    public BookController(BookService bookService, UserService userService, RequestService requestService, JwtService jwtService) {
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
        this.jwtService = jwtService;
    }

    @GetMapping("/title")
    public List<Book> getBooksByTitle(@RequestParam("title") String title) {
        return bookService.getBooksByTitle(title);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBook();
    }

    @PostMapping
    public Book createBook(@RequestHeader("Authorization") String token, @RequestBody Book book) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        return this.bookService.createBook(book);
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<Book> updateBook(@RequestHeader("Authorization") String token, @PathVariable Long bookId, @RequestBody Book updatedBook) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Book book = this.bookService.updateBookById(bookId, updatedBook);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, Boolean>> deleteBookB(@RequestHeader("Authorization") String token,@PathVariable Long bookId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Map<String, Boolean> response = bookService.deletBookById(bookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/author")
    public List<Book> getBooksByAuthor(@RequestParam("author") String author) {
        return bookService.getBooksByAuthor(author);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);

        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

}
