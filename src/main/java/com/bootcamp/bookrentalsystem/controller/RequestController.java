package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping()
    public ResponseEntity<Request> createRequest(@RequestParam("userId") Long userId,
                                                 @RequestParam("bookId") Long bookId,
                                                 @RequestParam("requestDuration") Long requestDuration) {
        Request createdRequest = requestService.createRequest(userId, bookId, requestDuration);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long requestId, @RequestBody Request updatedRequest) {
        Request request = this.requestService.updateRequestById(requestId, updatedRequest);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}