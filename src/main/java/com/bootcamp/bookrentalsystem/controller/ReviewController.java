package com.bootcamp.bookrentalsystem.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Review addReview(Review review) {
        return reviewService.addReview(review);
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
    public ResponseEntity<Review> updateReview(@PathVariable UUID reviewId, Review review) {
        Review updatedReview = this.reviewService.updateReview(reviewId, review);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    // Delete review by reviewId
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable UUID reviewId) {
        String response = reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}")
    public List<Review> getAllReviewsByBook(@PathVariable UUID bookId) {
        return reviewService.getAllReviewsByBook(bookId);
    }

}
