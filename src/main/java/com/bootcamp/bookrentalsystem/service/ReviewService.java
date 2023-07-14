package com.bootcamp.bookrentalsystem.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        return reviewRepository.findAll(sort);
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

        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        return reviewRepository.findByBookId(bookId, sort);
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

    public ResponseEntity<String> addReactionToReview(UUID reviewId, UUID userId, String action) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Set<UUID> likeUserIds = new HashSet<>(review.getLikeUserIds());
        Set<UUID> dislikeUserIds = new HashSet<>(review.getDislikeUserIds());

        boolean hasLiked = likeUserIds.contains(userId);
        boolean hasDisliked = dislikeUserIds.contains(userId);

        if ("like".equalsIgnoreCase(action)) {
            if (hasLiked) {
                throw new BadRequestException("User with ID: " + userId + " has already liked the review.");
            } else {
                if (hasDisliked) {
                    dislikeUserIds.remove(userId);
                }
                likeUserIds.add(userId);
            }
        } else if ("dislike".equalsIgnoreCase(action)) {
            if (hasDisliked) {
                throw new BadRequestException("User with ID: " + userId + " has already disliked the review.");
            } else {
                if (hasLiked) {
                    likeUserIds.remove(userId);
                }
                dislikeUserIds.add(userId);
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid action provided.");
        }

        review.setLikeUserIds(new ArrayList<>(likeUserIds));
        review.setDislikeUserIds(new ArrayList<>(dislikeUserIds));
        reviewRepository.save(review);
        return ResponseEntity.ok("User with ID: " + userId + " " + action + "d the review.");
    }

    public ResponseEntity<String> removeReactionFromReview(UUID reviewId, UUID userId, String action) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Set<UUID> likeUserIds = new HashSet<>(review.getLikeUserIds());
        Set<UUID> dislikeUserIds = new HashSet<>(review.getDislikeUserIds());

        boolean hasLiked = likeUserIds.contains(userId);
        boolean hasDisliked = dislikeUserIds.contains(userId);

        if ("like".equalsIgnoreCase(action)) {
            if (!hasLiked) {
                throw new BadRequestException("User with ID: " + userId + " has not liked the review.");
            }
            likeUserIds.remove(userId);
            review.setLikeUserIds(new ArrayList<>(likeUserIds));
            reviewRepository.save(review);
            return ResponseEntity.ok("User with ID: " + userId + " removed the like from the review.");

        } else if ("dislike".equalsIgnoreCase(action)) {
            if (!hasDisliked) {
                throw new BadRequestException("User with ID: " + userId + " has not disliked the review.");
            }
            dislikeUserIds.remove(userId);
            review.setDislikeUserIds(new ArrayList<>(dislikeUserIds));
            reviewRepository.save(review);
            return ResponseEntity.ok("User with ID: " + userId + " removed the dislike from the review.");

        } else {
            return ResponseEntity.badRequest().body("Invalid action provided.");
        }
    }
}
