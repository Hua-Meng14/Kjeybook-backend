package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public String get() {return "GET:: book controller";}

    @PostMapping
    public String create() {return "POST:: book controller";}

    @PatchMapping
    public String update() {return "UPDATE:: book controller";}

    @DeleteMapping
    public String delete() {return "DELETE:: book controller";}
}
