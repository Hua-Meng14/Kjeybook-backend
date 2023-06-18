package com.bootcamp.bookrentalsystem.model;

public class RejectRequest {
    private String reason;

    public RejectRequest() {
        // Default constructor
    }

    public RejectRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
