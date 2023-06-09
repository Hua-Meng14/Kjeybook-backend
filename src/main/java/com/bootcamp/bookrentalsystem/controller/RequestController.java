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

import java.util.List;
import java.util.Map;

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

    @GetMapping("/getAllRequests")
    public ResponseEntity<List<Request>> getAllRequests() {
        List<Request> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long requestId, @RequestBody Request updatedRequest) {
        Request request = this.requestService.updateRequestById(requestId, updatedRequest);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Map<String, Boolean>> deleteRequestById(@PathVariable Long requestId) {
        Map<String, Boolean> response = requestService.deleteRequestById(requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<Request>> getRequestsByUserId(@PathVariable Long userId) {
        List<Request> requests = requestService.getRequestsByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Request> acceptRequest(@PathVariable Long requestId) {
        Request acceptedRequest = requestService.acceptRequest(requestId);
        return new ResponseEntity<>(acceptedRequest, HttpStatus.OK);
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Request> rejectRequest(@PathVariable Long requestId) {
        Request rejectedRequest = requestService.rejectRequest(requestId);
        return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
    }

//    @PostMapping("/requests/{requestId}/notify")
//    public ResponseEntity<String> notifyUserRequestAccepted(@PathVariable Long requestId) {
//        userService.notifyUserRequestAccepted(requestId);
//        return ResponseEntity.ok("User notification sent successfully.");
//    }

}