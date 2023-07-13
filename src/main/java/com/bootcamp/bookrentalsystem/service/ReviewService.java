package com.bootcamp.bookrentalsystem.service;

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
import com.bootcamp.bookrentalsystem.model.Review;
import com.bootcamp.bookrentalsystem.repository.BookRepository;
import com.bootcamp.bookrentalsystem.repository.ReviewRepository;

@Component
@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private BookRepository bookRepository;

    @Autowired
    public ReviewService(@Qualifier("review") ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review addReview(Review review) {
        double rating = review.getRating();
        if (rating < 1 || rating > 5 || rating % 1 != 0) {
            throw new BadRequestException("Rating must be a whole number between 1 and 5 (inclusive)");
        }

        UUID bookId = review.getBookId();
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        return reviewRepository.save(review);
    }

    public Optional<Review> getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .map(Optional::of)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    }

    public Review updateReview(UUID reviewId, Review updatedReview) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        double rating = updatedReview.getRating();
        if (rating < 1 || rating > 5 || rating % 1 != 0) {
            throw new BadRequestException("Rating must be a whole number between 1 and 5 (inclusive)");
        }

        Optional.ofNullable(updatedReview.getComment())
                .ifPresent(existingReview::setComment);
        Optional.ofNullable(updatedReview.getRating())
                .ifPresent(existingReview::setRating);

        // Set isEdited to True
        existingReview.setEdited(true);

        return reviewRepository.save(existingReview);
    }

    public String deleteReview(UUID reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        reviewRepository.delete(existingReview);
        return "Remove deleted succesfully with ID: " + reviewId;
    }

    public List<Review> getAllReviewsByBook(UUID bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));
        return reviewRepository.findByBookId(bookId);
    }

}
