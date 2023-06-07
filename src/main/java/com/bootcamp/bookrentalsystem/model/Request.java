package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "_request")
@ApiModel(description = "Request details")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Request ID")
    private Long requestId;
    @ApiModelProperty(notes = "User whose this request belongs to")
    @ManyToOne
    private User borrower;
    @ApiModelProperty(notes = "Book whose this request belongs to")
    @ManyToOne
    private Book book;
    @ApiModelProperty(notes = "Request acceptance status")
    private String status;
    @ApiModelProperty(notes = "Request duration")
    private Long requestDuration;
    @ApiModelProperty(notes = "Date of request got accepted")
    private Date dateOfAccepted;
    @ApiModelProperty(notes = "Date of book to be returned")
    private Date dateOfReturn;

    public Request() {
        // Default constructor
    }

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
