package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "_user")

@ApiModel(description = "User details")
public class User {
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
    @ApiModelProperty(notes = "User's phone number")
    private String phoneNumber;
    @ApiModelProperty(notes = "User's profile image url")
    private String profileImg;
    @ApiModelProperty(notes = "User's role")
    private String role;

    public User(Long userId, String username, String password, String email, String phoneNumber, String profileImg, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImg = profileImg;
        this.role = role;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
