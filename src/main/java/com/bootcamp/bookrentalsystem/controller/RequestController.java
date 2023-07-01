package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.RejectRequest;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.service.JwtService;
import com.bootcamp.bookrentalsystem.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/request")
public class RequestController {
    private final RequestService requestService;
    private final JwtService jwtService;

    @Autowired
    public RequestController(RequestService requestService, JwtService jwtService) {
        this.requestService = requestService;
        this.jwtService = jwtService;
    }

    @PostMapping()
    public ResponseEntity<Request> createRequest(@RequestParam("userId") UUID userId,
            @RequestParam("bookId") Long bookId,
            @RequestParam("requestDuration") Long requestDuration,
            @RequestHeader("Authorization") String token) {
        // Validate User token access
        if (!jwtService.isValidUserToken(token, userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        Request createdRequest = requestService.createRequest(userId, bookId, requestDuration);
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests(@RequestHeader("Authorization") String token) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        List<Request> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/status")
    public List<Request> getRequestsByStatus(@RequestHeader("Authorization") String token,
            @RequestParam("status") String status,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {

        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }
        if (date == null) {
            return requestService.getRequestsByStatus(status);
        } else {
            return requestService.getRequestsByStatusAndDateOfRequest(status, date);
        }
    }

    @GetMapping("/book")
    public List<Request> getRequestsByBook(@RequestHeader("Authorization") String token,
            @RequestParam("bookId") Long bookId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }
        return requestService.getRequestsByBook(bookId);
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<Request> updateRequest(@RequestHeader("Authorization") String token,
            @PathVariable Long requestId, @RequestBody Request updatedRequest) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request request = this.requestService.updateRequestById(requestId, updatedRequest);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Map<String, Boolean>> deleteRequestById(@RequestHeader("Authorization") String token,
            @PathVariable Long requestId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Map<String, Boolean> response = requestService.deleteRequestById(requestId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Request>> getRequestsByUserId(@RequestHeader("Authorization") String token,
            @RequestParam("userId") UUID userId) {
        // Validate User token access
        if (!jwtService.isValidUserToken(token, userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        List<Request> requests = requestService.getRequestsByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<Request> acceptRequest(@RequestHeader("Authorization") String token,
            @PathVariable Long requestId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request acceptedRequest = requestService.acceptRequest(requestId);
        return new ResponseEntity<>(acceptedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Request> rejectRequest(@RequestHeader("Authorization") String token,
            @PathVariable Long requestId, @RequestBody RejectRequest rejectRequest) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Request rejectedRequest = requestService.rejectRequest(requestId, rejectRequest.getReason());
        return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/return")
    public ResponseEntity<String> returnBook(@RequestHeader("Authorization") String token,
            @PathVariable Long requestId) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        requestService.returnBook(requestId);
        return ResponseEntity.ok("Book returned successfully.");
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Request> getRequestByRequestId(@PathVariable Long requestId) {
        Request request = requestService.getRequestByRequestId(requestId);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Map<String, Integer>>> countRequestByStatus(
            @RequestHeader("Authorization") String token) {
        // Validate and decode the JWT token
        if (!jwtService.isValidAdminToken(token)) {
            throw new ForbiddenException("Access Denied!!");
        }

        Map<String, Map<String, Integer>> statusCounts = requestService.countRequestsByStatus();
        return ResponseEntity.ok(statusCounts);
    }

}