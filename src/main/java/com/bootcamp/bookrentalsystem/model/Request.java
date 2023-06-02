package com.bootcamp.bookrentalsystem.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @ManyToOne
    private User borrower;
    @ManyToOne
    private Book book;
    private String status;
    private Long requestDuration;
    private Date dateOfAccepted;
    private Date dateOfReturn;

    public Request(Long requestId, User borrower, Book book, String status, Long requestDuration, Date dateOfAccepted, Date dateOfReturn) {
        this.requestId = requestId;
        this.borrower = borrower;
        this.book = book;
        this.status = status;
        this.requestDuration = requestDuration;
        this.dateOfAccepted = dateOfAccepted;
        this.dateOfReturn = dateOfReturn;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRequestDuration() {
        return requestDuration;
    }

    public void setRequestDuration(Long requestDuration) {
        this.requestDuration = requestDuration;
    }

    public Date getDateOfAccepted() {
        return dateOfAccepted;
    }

    public void setDateOfAccepted(Date dateOfAccepted) {
        this.dateOfAccepted = dateOfAccepted;
    }

    public Date getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(Date dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }
}
