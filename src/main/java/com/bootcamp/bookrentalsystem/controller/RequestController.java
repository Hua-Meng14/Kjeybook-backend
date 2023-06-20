package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.RejectRequest;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.service.BookService;
import com.bootcamp.bookrentalsystem.service.JwtService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import com.bootcamp.bookrentalsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/request")
public class RequestController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;
    private final JwtService jwtService;

    @Autowired
    public RequestController(BookService bookService, UserService userService, RequestService requestService, JwtService jwtService) {
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
        this.jwtService = jwtService;
    }

    @PostMapping()
    public ResponseEntity<Request> createRequest(@RequestParam("userId") Long userId,
                                                 @RequestParam("bookId") Long bookId,
                                                 @RequestParam("requestDuration") Long requestDuration,
                                                 @RequestHeader("Authorization") String token) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        Request createdRequest = requestService.createRequest(userId, bookId, requestDuration);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        List<Request> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/status")
    public List<Request> getRequestsByStatus(@RequestParam("status") String status, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return requestService.getRequestsByStatusAndDateOfRequest(status, date);
    }

    @GetMapping("/book")
    public List<Request> getRequestsByBook(@RequestParam("bookId") Long bookId) {
        return requestService.getRequestsByBook(bookId);
    }


    @PatchMapping("/{requestId}")
    public ResponseEntity<Request> updateRequest(@RequestHeader("Authorization") String token, @PathVariable Long requestId, @RequestBody Request updatedRequest) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request request = this.requestService.updateRequestById(requestId, updatedRequest);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Map<String, Boolean>> deleteRequestById(@RequestHeader("Authorization") String token, @PathVariable Long requestId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Map<String, Boolean> response = requestService.deleteRequestById(requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/user")
    public ResponseEntity<List<Request>> getRequestsByUserId(@RequestHeader("Authorization") String token, @RequestParam("userId") Long userId) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        List<Request> requests = requestService.getRequestsByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<Request> acceptRequest(@RequestHeader("Authorization") String token, @PathVariable Long requestId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request acceptedRequest = requestService.acceptRequest(requestId);
        return new ResponseEntity<>(acceptedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Request> rejectRequest(@RequestHeader("Authorization") String token, @PathVariable Long requestId, @RequestBody RejectRequest rejectRequest) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request rejectedRequest = requestService.rejectRequest(requestId, rejectRequest.getReason());
        return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/return")
    public ResponseEntity<String> returnBook(@RequestHeader("Authorization") String token, @PathVariable Long requestId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request archivedRequest = requestService.returnBook(requestId);
        return ResponseEntity.ok("Book returned successfully.");
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Request> getRequestByRequestId(@PathVariable Long requestId){
        Request request = requestService.getRequestByRequestId(requestId);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }


}