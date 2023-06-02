package com.bootcamp.bookrentalsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String title;
    private String author;
    private String category;
    private String bookImg;
    private String description;
    private Long maximumRequestPeriod;

    public Book(Long bookId, String title, String author, String category, String bookImg, String description, Long maximumRequestPeriod) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.bookImg = bookImg;
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
}
