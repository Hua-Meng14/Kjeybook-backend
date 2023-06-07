package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;

    @Autowired

    public UserController(BookService bookService, UserService userService, RequestService requestService) {
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
    }


    @GetMapping
    public String get() {return "GET:: user controller";}

    @PatchMapping
    public String update() {return "UPDATE:: user controller";}

    @DeleteMapping
    public String delete() {return "DELETE:: user controller";}

    @PostMapping
    public User createUser(User user) {
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
}
