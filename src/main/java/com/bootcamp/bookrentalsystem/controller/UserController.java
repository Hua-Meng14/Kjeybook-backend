package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
