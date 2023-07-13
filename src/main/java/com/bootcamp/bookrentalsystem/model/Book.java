package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "_book")
@ApiModel(description = "User details")
public class Book {

    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Book ID")
    private UUID bookId;
    @ApiModelProperty(notes = "Book Title")
    private String title;
    @ApiModelProperty(notes = "Book Author")
    private String author;
    @ApiModelProperty(notes = "Book Category")
    private String category;
    @ApiModelProperty(notes = "Book Image URL")
    private String bookImg;
    @ApiModelProperty(notes = "Book Description")
    private String description;
    @ApiModelProperty(notes = "Book Rental status")
    private Boolean isRented = false;
    @ApiModelProperty(notes = "Book Rental delete status")
    private Boolean isDeleted = false;

    // @ApiModelProperty(notes = "Book maximum request duration")
    // private Long maximumRequestPeriod;
    @ApiModelProperty(notes = "User favorite book mapping.")
    @ManyToMany(mappedBy = "favoriteBooks")
    private List<User> users;
    // @ApiModelProperty(notes = "Requests book mapping.")
    // @OneToMany(mappedBy = "book")
    // private List<Request> requests;

    @ApiModelProperty(notes = "Book Reviews")
    @OneToMany(mappedBy = "bookId", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Book() {
        // Default constructor
    }

    public Book(UUID bookId, String title, String author, String category, String bookImg, Boolean isRented,
            String description, Boolean isDeleted, List<Review> reviews) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.bookImg = bookImg;
        this.isRented = isRented;
        this.description = description;
        this.isDeleted = isDeleted;
        this.reviews = reviews;
        // this.maximumRequestPeriod = maximumRequestPeriod;
    }

    public UUID getId() {
        return bookId;
    }

    public void setId(UUID bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBookImg() {
        return bookImg;
    }

    public void setBookImg(String bookImg) {
        this.bookImg = bookImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // public Long getMaximumRequestPeriod() {
    // return maximumRequestPeriod;
    // }

    // public void setMaximumRequestPeriod(Long maximumRequestPeriod) {
    // this.maximumRequestPeriod = maximumRequestPeriod;
    // }

    public Boolean getRented() {
        return isRented;
    }

    public void setRented(Boolean rented) {
        isRented = rented;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviewList) {
        reviews = reviewList;
    }
}
