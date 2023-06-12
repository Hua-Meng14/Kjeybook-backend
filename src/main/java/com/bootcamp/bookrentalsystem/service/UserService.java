package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.*;
import com.bootcamp.bookrentalsystem.exception.IllegalStateException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.ChangePasswordRequest;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Service
public class UserService {
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private RequestRepository requestRepository;
    private EmailService emailService;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(@Qualifier("user") UserRepository userRepository, BookRepository bookRepository, RequestRepository requestRepository, EmailService emailService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.requestRepository = requestRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserById(Long userId, String jwtToken) {

//         Validate and decode the JWT token
//        System.out.println("--------- IS TOKEN ROLE ADMIN: " + jwtService.isAdminToken(jwtToken));
        if (!jwtService.isValidAdminToken(jwtToken)) {
            throw new ForbiddenException("Access Denied!!");
        }

//        if (!jwtService.isUserToken(jwtToken, userId)) {
//            throw new UnauthorizedException("Unauthorized Access!!");
//        }

//        System.out.println("--------- IS TOKEN EXPIRED: " + jwtService.isTokenExpired(jwtToken));
//        if (jwtService.isTokenExpired(jwtToken)) {
//            throw new BadRequestException("Invalid Token!!");
//        }

        return userRepository.findById(userId)
                .map(Optional::of)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }


    public User updateUserById(Long userId, User updatedUser) {

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

    public Map<String, Boolean> deleteUser(Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.deleteById(userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Book> getFavoriteBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getFavoriteBooks();
    }

    public void addBookToUserFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        List<Book> favoriteBooks = user.getFavoriteBooks();

        // Check if the book is already in the user's favorite list
        if (favoriteBooks.contains(book)) {
            throw new IllegalStateException("Book is already added to the user's favorites.");
        }

        // Add the book to the user's favorite list
        favoriteBooks.add(book);
        userRepository.save(user);
    }

    public void removeBookFromFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Book> favoriteBooks = user.getFavoriteBooks();

        // Check if the book exists in the user's favorite list
        boolean bookExists = favoriteBooks.stream()
                .anyMatch(book -> book.getId().equals(bookId));

        if (!bookExists) {
            throw new ResourceNotFoundException("Book not found in the user's favorites with book id: " + bookId);
        }

//        System.out.println("---------------BEFORE DELETE: "+ user.getFavoriteBooks());

        // Remove the book from the user's favorite list
        favoriteBooks.removeIf(book -> book.getId().equals(bookId));

//        System.out.println("---------------AFTER DELETE: "+ user.getFavoriteBooks());

        userRepository.save(user);
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

    public String changePassword(Long userId, ChangePasswordRequest request) {

        // Retrieve the user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+userId));

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
}
