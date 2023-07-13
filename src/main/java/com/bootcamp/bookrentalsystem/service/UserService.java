package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.*;
import com.bootcamp.bookrentalsystem.exception.IllegalStateException;
import com.bootcamp.bookrentalsystem.model.*;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Service
public class UserService {
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private RequestRepository requestRepository;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Qualifier("user") UserRepository userRepository, BookRepository bookRepository,
            RequestRepository requestRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.requestRepository = requestRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Cacheable(value = "usersById", key = "#userId")
    public Optional<User> findUserById(UUID userId) {

        // if (!jwtService.isUserToken(jwtToken, userId)) {
        // throw new UnauthorizedException("Unauthorized Access!!");
        // }

        // System.out.println("--------- IS TOKEN EXPIRED: " +
        // jwtService.isTokenExpired(jwtToken));
        // if (jwtService.isTokenExpired(jwtToken)) {
        // throw new BadRequestException("Invalid Token!!");
        // }

        return userRepository.findById(userId)
                .map(Optional::of)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    @CachePut(value = "usersById", key = "#userId")
    public User updateUserById(UUID userId, User updatedUser) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional.ofNullable(updatedUser.getUsername())
                .ifPresent(existingUser::setUsername);
        Optional.ofNullable(updatedUser.getEmail())
                .ifPresent(existingUser::setEmail);
        Optional.ofNullable(updatedUser.getPhoneNumber())
                .ifPresent(existingUser::setPhoneNumber);
        Optional.ofNullable(updatedUser.getProfileImg())
                .ifPresent(existingUser::setProfileImg);

        return userRepository.save(existingUser);
    }

    @CacheEvict(value = "usersById", key = "#userId")
    public Map<String, Boolean> deleteUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.deleteById(userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @Cacheable("allUsers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "favoriteBooksByUserId", key = "#userId")
    public List<Book> getFavoriteBooks(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getFavoriteBooks();
    }

    @CacheEvict(value = "favoriteBooksByUserId", allEntries = true)
    public void evictFavoriteBooksCache(UUID userId) {
        // This method is used to invalidate the cache for getFavoriteBooks() method
        // No implementation is needed as the @CacheEvict annotation takes care of cache
        // eviction
    }

    @CacheEvict(value = {"favoriteBooksByUserId", "usersById"}, key = "#userId", allEntries = true)
    public void addBookToUserFavorites(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        List<Book> favoriteBooks = user.getFavoriteBooks();

        // Check if the book is already in the user's favorite list
        if (favoriteBooks.contains(book)) {
            throw new IllegalStateException("Book already in favorited list.");
        }

        // Add the book to the user's favorite list
        favoriteBooks.add(book);
        userRepository.save(user);

        // Invalidate the cache for getFavoriteBooks() method
        // evictFavoriteBooksCache(userId);
    }

    @CacheEvict(value = {"favoriteBooksByUserId", "usersById"}, key = "#userId", allEntries = true)
    public void removeBookFromFavorites(UUID userId, UUID bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Book> favoriteBooks = user.getFavoriteBooks();

        // Check if the book exists in the user's favorite list
        boolean bookExists = favoriteBooks.stream()
                .anyMatch(book -> book.getId().equals(bookId));

        if (!bookExists) {
            throw new ResourceNotFoundException("Book not found in the user's favorites with book id: " + bookId);
        }

        // System.out.println("---------------BEFORE DELETE: "+
        // user.getFavoriteBooks());

        // Remove the book from the user's favorite list
        favoriteBooks.removeIf(book -> book.getId().equals(bookId));

        // System.out.println("---------------AFTER DELETE: "+ user.getFavoriteBooks());

        userRepository.save(user);

        // Invalidate the cache for getFavoriteBooks() method
        // evictFavoriteBooksCache(userId);
    }

    public void notifyUserRequestAccepted(Long requestId) {
        Request acceptedRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Send the email notification
        emailService.sendRequestAcceptedEmail(acceptedRequest);

    }

    public void notifyUserRequestRejected(Long requestId) {
        Request rejectedRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Send the email notification
        emailService.sendRequestRejectedEmail(rejectedRequest);

    }

    public String changePassword(UUID userId, ChangePasswordRequest request) {

        // Retrieve the user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate the old password
        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            throw new BadRequestException("Incorrect Password!!");
        }

        // Update the password with the new password
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(existingUser);

        // Return a success message
        return "Password changed successfully!";
    }

    // Utility method to generate a password reset token (you can customize this as
    // needed)
    private String generateResetToken() {
        // Implement your logic to generate a unique token
        // You can use UUID or any other secure random generator
        return UUID.randomUUID().toString();
    }

    public void saveResetPwdToken(User user, String resetPwdToken, LocalDateTime expirationTime) {
        // Set the reset token and expiration time for the user
        user.setResetPwdToken(resetPwdToken);
        user.setResetPwdExpirationTime(expirationTime);
        userRepository.save(user);
    }

    public ResponseEntity<String> forgotPassword(String email) {

        // Validate the request payload
        if (email.isEmpty()) {
            return new ResponseEntity<String>("Email is required.", HttpStatus.BAD_REQUEST);
        }

        // Check if user exists
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Generate a password reset token (you can use a library or custom
        // implementation)
        String resetPwdToken = generateResetToken();

        // Set the expiration time (e.g., 1 hour from the current time)
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(1);

        // Save the reset token for the user (in your database or cache)
        saveResetPwdToken(existingUser, resetPwdToken, expirationTime);

        // Send the password reset email
        emailService.sendResetPasswordEmail(existingUser.getEmail(), resetPwdToken, expirationTime);

        return ResponseEntity.ok("Password reset instructions sent to your email.");

    }

    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        String resetPwdToken = request.getResetPwdToken();
        String newPassword = request.getNewPassword();

        // Validate the request payload
        if (resetPwdToken == null || resetPwdToken.isEmpty()) {
            throw new BadRequestException("Reset token is required.");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BadRequestException("New password is required.");
        }

        // Check if the reset token is valid and retrieve the associated user
        User user = userRepository.findByResetPwdToken(resetPwdToken)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized Access"));

        // Check if the reset token has expired
        if (user.getResetPwdExpirationTime() == null
                || user.getResetPwdExpirationTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid Token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPwdToken(null);
        user.setResetPwdExpirationTime(null);

        // Save new password
        userRepository.save(user);
        // Send confirmation for new password to user
        // emailService.sendResetPasswordSuccessEmail(user);

        return ResponseEntity.ok("Password Reset Succesfully");
    }

    public ResponseEntity<String> validateResetPwdToken(String resetPwdToken) {

        User existingUser = userRepository.findByResetPwdToken(resetPwdToken)
                .orElseThrow(() -> new BadRequestException("Invalid Reset Password Token"));

        // Check if the reset token has expired
        if (existingUser.getResetPwdExpirationTime() == null
                || existingUser.getResetPwdExpirationTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Invalid Reset Password Token");
        }
        return ResponseEntity.ok("Reset Password Token is valid");
    }
}
