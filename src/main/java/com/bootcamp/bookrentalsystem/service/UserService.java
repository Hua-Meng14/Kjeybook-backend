package com.bootcamp.bookrentalsystem.service;

import com.bootcamp.bookrentalsystem.exception.*;
import com.bootcamp.bookrentalsystem.exception.IllegalStateException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.Request;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.RequestRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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


    @Autowired
    public UserService(@Qualifier("user") UserRepository userRepository, BookRepository bookRepository, RequestRepository requestRepository, EmailService emailService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.requestRepository = requestRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findUserById(Long userId, String token) {
        if (token.isEmpty()) {
            throw new UnauthorizedException("Token required");
        }
        if (!token.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid token format");
        }
        String jwtToken = token.substring(7);

        // Validate and decode the JWT token
        System.out.println("--------- IS TOKEN ROLE ADMIN: " + jwtService.isAdminToken(token));
        if (!jwtService.isAdminToken(jwtToken)) {
            throw new ForbiddenException("Access Denied!!");
        }

        System.out.println("--------- IS TOKEN EXPIRED: " + jwtService.isTokenExpired(token));
        if (jwtService.isTokenExpired(jwtToken)) {
            throw new BadRequestException("Invalid Token!!");
        }

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

        // Add the book to the user's favorite list
        user.getFavoriteBooks().add(book);
        userRepository.save(user);
    }

    public void removeBookFromFavorites(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Book> favoriteBooks = user.getFavoriteBooks();

        // Check if the user's favorite list is empty
        if (favoriteBooks.isEmpty()) {
            throw new IllegalStateException("Cannot remove book from an empty favorite list.");
        }

        if (!favoriteBooks.stream().anyMatch(book -> book.getId().equals(bookId))) {
            throw new ResourceNotFoundException("Book not found in user's favorite with book id: " + bookId);
        }

        Optional<Book> book = bookRepository.findById(bookId);
        // Remove the book from the user's favorite list
        user.getFavoriteBooks().remove(book);
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

}
