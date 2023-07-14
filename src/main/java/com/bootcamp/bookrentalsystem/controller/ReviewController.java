package com.bootcamp.bookrentalsystem.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bootcamp.bookrentalsystem.model.Review;
import com.bootcamp.bookrentalsystem.service.ReviewService;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Post Create review
    @PostMapping
    public Review addReview(@RequestHeader("Authorization") String token, @RequestParam("userId") UUID userId,
            @RequestParam("bookId") UUID bookId, @RequestParam("rating") int rating,
            @RequestParam("comment") String comment) {
        return reviewService.addReview(token, userId, bookId, rating, comment);
    }

    // Get all reviews
    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // Get review by reviewId
    // @GetMapping("/{reviewId}")
    // public Review getReviewById(){
    // return reviewService.
    // }

    // Update review by reviewId
    @PatchMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@RequestHeader("Authorization") String token,
            @PathVariable UUID reviewId, @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String comment) {
        Optional<Integer> ratingOptional = Optional.ofNullable(rating);
        Optional<String> commentOptional = Optional.ofNullable(comment);
        Review updatedReview = reviewService.updateReview(token, reviewId, ratingOptional, commentOptional);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    // Delete review by reviewId
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@RequestHeader("Authorization") String token,
            @PathVariable UUID reviewId) {
        String response = reviewService.deleteReview(token, reviewId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getAllReviewsByBook(@PathVariable UUID bookId) {
        return reviewService.getAllReviewsByBook(bookId);
    }

    @PostMapping("/{reviewId}/reaction")
    public Review addReactionToReview(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID reviewId,
            @RequestParam UUID userId,
            @RequestParam String action) {
        return reviewService.addReactionToReview(token, reviewId, userId, action);
    }

    @DeleteMapping("/{reviewId}/reaction")
    public Review removeReactionFromReview(@RequestHeader("Authorization") String token, @PathVariable UUID reviewId, @RequestParam UUID userId,
            @RequestParam String action) {
        return reviewService.removeReactionFromReview(token, reviewId, userId, action);
    }

}
