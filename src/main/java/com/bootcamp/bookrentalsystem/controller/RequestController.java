package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/request")
public class RequestController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;

    @Autowired
    public RequestController(BookService bookService, UserService userService, RequestService requestService) {
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
    }


    @GetMapping
    public String get() {return "GET:: request controller";}

    @PostMapping
    public String create() {return "POST:: request controller";}

    @PatchMapping
    public String update() {return "UPDATE:: request controller";}

    @DeleteMapping
    public String delete() {return "DELETE:: request controller";}
}