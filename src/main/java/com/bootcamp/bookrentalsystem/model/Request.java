package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.time.LocalDate;
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
    @JoinColumn(name = "book_id")
    private Book book;
    @ApiModelProperty(notes = "Request acceptance status")
    private String status = "PENDING";
    @ApiModelProperty(notes = "Request isApproved status")
    private Boolean isApproved = false;
    @ApiModelProperty(notes = "Reason for rejecting request")
    private String rejectedReason;
    @ApiModelProperty(notes = "Date of request submitted")
    private LocalDate dateOfRequest;
    @ApiModelProperty(notes = "Request duration")
    private Long requestDuration;
    @ApiModelProperty(notes = "Date of request got accepted")
    private LocalDate dateOfAccepted = null;
    @ApiModelProperty(notes = "Date of request got rejected")
    private LocalDate dateOfRejected = null;
    @ApiModelProperty(notes = "Date of book to be returned")
    private LocalDate dateOfReturn = null;
    @ApiModelProperty(notes = "Date of book returned back to library")
    private LocalDate dateOfReceived = null;

    public Request() {
        // Default constructor
    }

    public Request(Long requestId, User borrower, Book book, String status, Long requestDuration, LocalDate dateOfAccepted, LocalDate dateOfReturn, LocalDate dateOfRequest, LocalDate dateOfRejected, Boolean isApproved, LocalDate dateOfReceived, String rejectedReason) {
        this.requestId = requestId;
        this.borrower = borrower;
        this.book = book;
        this.status = status;
        this.requestDuration = requestDuration;
        this.dateOfAccepted = dateOfAccepted;
        this.dateOfReturn = dateOfReturn;
        this.dateOfRequest = dateOfRequest;
        this.dateOfRejected = dateOfRejected;
        this.isApproved = isApproved;
        this.dateOfReceived = dateOfReceived;
        this.rejectedReason = rejectedReason;
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

    public LocalDate getDateOfAccepted() {
        return dateOfAccepted;
    }

    public void setDateOfAccepted(LocalDate dateOfAccepted) {
        this.dateOfAccepted = dateOfAccepted;
    }

    public LocalDate getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(LocalDate dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public LocalDate getDateOfRequest() {
        return dateOfRequest;
    }

    public void setDateOfRequest(LocalDate dateOfRequest) {
        this.dateOfRequest = dateOfRequest;
    }

    public LocalDate getDateOfRejected() {
        return dateOfRejected;
    }

    public void setDateOfRejected(LocalDate dateOfRejected) {
        this.dateOfRejected = dateOfRejected;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean accepted) {
        isApproved = accepted;
    }

    public LocalDate getDateOfReceived() {
        return dateOfReceived;
    }

    public void setDateOfReceived(LocalDate dateOfReceived) {
        this.dateOfReceived = dateOfReceived;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }
}
