package com.bootcamp.bookrentalsystem.model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "_book")
@ApiModel(description = "User details")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Book ID")
    private Long bookId;
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
    @ApiModelProperty(notes = "Book maximum request duration")
    private Long maximumRequestPeriod;
    @ApiModelProperty(notes = "User favorite book mapping.")
    @ManyToMany(mappedBy = "favoriteBooks")
    private List<User> users;

    public Book() {
        // Default constructor
    }

    public Book(Long bookId, String title, String author, String category, String bookImg,Boolean isRented, String description, Long maximumRequestPeriod) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.bookImg = bookImg;
        this.isRented = isRented;
        this.description = description;
        this.maximumRequestPeriod = maximumRequestPeriod;
    }

    public Long getId() {
        return bookId;
    }

    public void setId(Long bookId) {
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

    public Long getMaximumRequestPeriod() {
        return maximumRequestPeriod;
    }

    public void setMaximumRequestPeriod(Long maximumRequestPeriod) {
        this.maximumRequestPeriod = maximumRequestPeriod;
    }

    public Boolean getRented() {
        return isRented;
    }

    public void setRented(Boolean rented) {
        isRented = rented;
    }
}
