package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.EmailRequest;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.EmailService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;
    private final EmailService emailService;

    @Autowired

    public UserController(BookService bookService, UserService userService, RequestService requestService, EmailService emailService) {
        this.emailService = emailService;
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return this.userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return this.userService.findUserById(userId)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUserById(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = this.userService.updateUserById(userId, updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<User> deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}/favorites")
    public List<Book> getUserFavoriteList(@PathVariable Long userId) {
        return userService.getFavoriteBooks(userId);
    }

    @DeleteMapping
    public String delete() {return "DELETE:: user controller";}

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/{userId}/favorites/{bookId}")
    public ResponseEntity<String> addBookToUserFavorites(@PathVariable Long userId, @PathVariable Long bookId) {
        userService.addBookToUserFavorites(userId, bookId);
        return ResponseEntity.ok("Book added to user's favorite list.");
    }

    @DeleteMapping("/{userId}/favorites/{bookId}")
    public ResponseEntity<String> removeBookFromUserFavorites(@PathVariable Long userId, @PathVariable Long bookId) {
        userService.removeBookFromFavorites(userId, bookId);
        return ResponseEntity.ok("Book removed from user's favorite list.");
    }
}
