package com.bootcamp.bookrentalsystem.controller;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ForbiddenException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.ChangePasswordRequest;
import com.bootcamp.bookrentalsystem.model.ResetPasswordRequest;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.service.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final BookService bookService;
    private final UserService userService;
    private final RequestService requestService;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Autowired

    public UserController(BookService bookService, UserService userService, RequestService requestService, EmailService emailService, JwtService jwtService) {
        this.emailService = emailService;
        this.bookService = bookService;
        this.userService = userService;
        this.requestService = requestService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return this.userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return this.userService.findUserById(userId).map(book -> new ResponseEntity<>(book, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUserById(@RequestHeader("Authorization") String token, @PathVariable Long userId, @RequestBody User updatedUser) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        User user = this.userService.updateUserById(userId, updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<User> deleteUserById(@RequestHeader("Authorization") String token, @PathVariable Long userId) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}/favorites")
    public List<Book> getUserFavoriteList(@RequestHeader("Authorization") String token, @PathVariable Long userId) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        return userService.getFavoriteBooks(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}/favorites/{bookId}")
    public ResponseEntity<String> addBookToUserFavorites(@RequestHeader("Authorization") String token, @PathVariable Long userId, @PathVariable Long bookId) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        userService.addBookToUserFavorites(userId, bookId);
        return ResponseEntity.ok("Book added to user's favorite list.");
    }

    @DeleteMapping("/{userId}/favorites/{bookId}")
    public ResponseEntity<String> removeBookFromUserFavorites(@RequestHeader("Authorization") String token, @PathVariable Long userId, @PathVariable Long bookId) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        userService.removeBookFromFavorites(userId, bookId);
        return ResponseEntity.ok("Book removed from user's favorite list.");
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        // Validate User token access
        if(!jwtService.isValidUserToken(token,userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        return ResponseEntity.ok(userService.changePassword(userId, request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return userService.forgotPassword(email);

    }

    @PostMapping("/forgot-password/update")
    public ResponseEntity<String> resetNewPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}
