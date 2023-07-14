package com.bootcamp.bookrentalsystem.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bootcamp.bookrentalsystem.exception.BadRequestException;
import com.bootcamp.bookrentalsystem.exception.ResourceNotFoundException;
import com.bootcamp.bookrentalsystem.exception.UnauthorizedException;
import com.bootcamp.bookrentalsystem.model.Book;
import com.bootcamp.bookrentalsystem.model.Review;
import com.bootcamp.bookrentalsystem.model.User;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.ReviewRepository;
import com.bootcamp.bookrentalsystem.repository.UserRepository;

import io.jsonwebtoken.Claims;

@Component
@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;
    private JwtService jwtService;
    private UserRepository userRepository;

    @Autowired
    public ReviewService(@Qualifier("review") ReviewRepository reviewRepository, BookRepository bookRepository,
            JwtService jwtService, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review addReview(String token, UUID userId, UUID bookId, int rating, String comment) {
        Claims claims = jwtService.extractClaimsFromToken(token);
        UUID tokenUserId = UUID.fromString(claims.get("id", String.class));
        if (!tokenUserId.equals(userId)) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: "));

        // Check if the user has already posted a review for this book
        List<Review> reviews = getAllReviewsByBook(bookId);
        for (Review existingReview : reviews) {
            if (existingReview.getReveiwer().getUserId().equals(userId)) {
                throw new BadRequestException("You have already posted a review for this book");
            }
        }

        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be a whole number between 1 and 5 (inclusive)");
        }

        Review review = new Review();
        review.setReveiwer(existingUser);
        review.setBookid(bookId);
        review.setComment(comment);
        review.setRating(rating);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        review.setTimestamp(LocalDateTime.now().plusHours(7).format(formatter)); // Convert to ICT

        reviewRepository.save(review);

        Map<Double, Integer> result = calculateOverallRating(review.getBookId());

        double updateBookOverAllRating = result.keySet().iterator().next();
        int numberOfReviews = result.get(updateBookOverAllRating);
        book.setOverAllRating(updateBookOverAllRating);
        book.setReviewsCount(numberOfReviews);
        bookRepository.save(book);

        return review;
    }

    public Optional<Review> getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .map(Optional::of)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    }

    public Review updateReview(String token, UUID reviewId, Optional<Integer> ratingOptional,
            Optional<String> commentOptional) {

        Claims claims = jwtService.extractClaimsFromToken(token);
        UUID userId = UUID.fromString(claims.get("id", String.class));
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!userId.equals(existingReview.getReveiwer().getUserId())) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        ratingOptional.ifPresent(rating -> {
            if (rating < 1 || rating > 5) {
                throw new BadRequestException("Rating must be a whole number between 1 and 5 (inclusive)");
            }
            existingReview.setRating(rating);
        });

        commentOptional.ifPresent(existingReview::setComment);

        // Set isEdited to True
        existingReview.setEdited(true);

        return reviewRepository.save(existingReview);
    }

    public String deleteReview(String token, UUID reviewId) {
        Claims claims = jwtService.extractClaimsFromToken(token);
        UUID userId = UUID.fromString(claims.get("id", String.class));
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        if (!userId.equals(existingReview.getReveiwer().getUserId())) {
            throw new UnauthorizedException("Unauthorized Access");
        }

        reviewRepository.delete(existingReview);
        return "Review deleted successfully with ID: " + reviewId;
    }

    public List<Review> getAllReviewsByBook(UUID bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));
        return reviewRepository.findByBookId(bookId);
    }

    public Map<Double, Integer> calculateOverallRating(UUID bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));
        List<Review> reviews = getAllReviewsByBook(bookId);
        if (reviews == null || reviews.isEmpty()) {
            Map<Double, Integer> result = new HashMap<>();
            result.put(0.0, 0);
            return result;
        }

        int totalRatings = 0;
        int numberOfReviews = reviews.size();

        for (Review review : reviews) {
            totalRatings += review.getRating();
        }
        double averageRating = (double) totalRatings / numberOfReviews;

        Map<Double, Integer> result = new HashMap<>();
        result.put(averageRating, numberOfReviews);
        return result;

    }

}
