package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;


@Entity
@Table(name = "_user")
@ApiModel(description = "Register User details")
public class RegisterUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "User ID")
    private Long userId;
    @ApiModelProperty(notes = "User's username")
    private String username;
    @ApiModelProperty(notes = "User's password")
    private String password;
    @ApiModelProperty(notes = "User's email address")
    private String email;
    @ApiModelProperty(notes = "User's role")
    private String role;
    @ApiModelProperty(notes = "User's phone number")
    private String phoneNumber;
    public RegisterUser() {
        // Default constructor for Jackson deserialization
    }

    public RegisterUser(String username, String email, String encodedPassword, String role, String phoneNumber) {
        // Constructor for convenience
        this.username = username;
        this.email = email;
        this.password = encodedPassword;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}