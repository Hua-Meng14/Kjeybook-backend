package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@ApiModel(description = "Book review")
public class Review {

    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Review ID")
    private UUID reviewId;
    
    @ApiModelProperty(notes = "Review comment")
    private String comment;
    
    @ApiModelProperty(notes = "Review Start rating")
    private int rating;

    @ApiModelProperty(notes = "Review upVotes")
    private int upVotes;
    
    @ApiModelProperty(notes = "Review downVotes")
    private int downVotes;

    @ApiModelProperty(notes = "Flag indicating whether the review has been edited")
    private boolean isEdited;

    @Column(columnDefinition = "uuid")
    @ApiModelProperty(notes = "Book ID")
    private UUID bookId;
    
    public Review() {
        // Default constructor
    }

    public Review(UUID reviewId, String comment, int rating, int upVotes, int downVotes, boolean isEdited) {
        this.reviewId = reviewId;
        this.comment = comment;
        this.rating = rating;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.isEdited = isEdited;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    

 }