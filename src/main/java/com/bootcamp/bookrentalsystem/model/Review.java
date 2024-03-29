package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.util.List;
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

    @ElementCollection
    @CollectionTable(name = "review_likes", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "user_id")
    @ApiModelProperty(notes = "User IDs who clicked like on the review")
    private List<UUID> likeUserIds;

    @ElementCollection
    @CollectionTable(name = "review_dislikes", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "user_id")
    @ApiModelProperty(notes = "User IDs who clicked dislike on the review")
    private List<UUID> dislikeUserIds;

    @ApiModelProperty(notes = "Flag indicating whether the review has been edited")
    private boolean isEdited;

    @ApiModelProperty(notes = "Date of review posted")
    private String timestamp = null;

    @Column(columnDefinition = "uuid")
    @ApiModelProperty(notes = "Book ID")
    private UUID bookId;

    // @Column(columnDefinition = "uuid")
    @ApiModelProperty(notes = "User ID")
    @ManyToOne
    private User reveiwer;

    public Review() {
        // Default constructor
    }

    public Review(UUID reviewId, String comment, int rating, List<UUID> likeUserIds, List<UUID> dislikeUserIds, boolean isEdited,
            String timestamp) {
        this.reviewId = reviewId;
        this.comment = comment;
        this.rating = rating;
        this.likeUserIds = likeUserIds;
        this.dislikeUserIds = dislikeUserIds;
        this.isEdited = isEdited;
        this.timestamp = timestamp;
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
    
    public List<UUID> getLikeUserIds() {
        return likeUserIds;
    }

    public void setLikeUserIds(List<UUID> likeUserIds) {
        this.likeUserIds = likeUserIds;
    }

    public List<UUID> getDislikeUserIds() {
        return dislikeUserIds;
    }

    public void setDislikeUserIds(List<UUID> dislikeUserIds) {
        this.dislikeUserIds = dislikeUserIds;
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

    public void setBookid(UUID bookId) {
        this.bookId = bookId;
    }

    

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public User getReveiwer() {
        return reveiwer;
    }

    public void setReveiwer(User reveiwer) {
        this.reveiwer = reveiwer;
    }

}