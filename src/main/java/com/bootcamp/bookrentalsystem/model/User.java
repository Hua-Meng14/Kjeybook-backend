package com.bootcamp.bookrentalsystem.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "_user")
@ApiModel(description = "User details")
public class User {
    @Id
    @Column(columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "User ID")
    private UUID userId;
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
    @ManyToMany
    @JoinTable(
            name = "user_favorite_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @ApiModelProperty(notes = "User's favorite books list")
    private List<Book> favoriteBooks;

    @ApiModelProperty(notes = "User's reset password token")
    private String resetPwdToken;

    @ApiModelProperty(notes = "User's reset password token expiration time")
    private LocalDateTime resetPwdExpirationTime;



    public User() {
        // Default constructor for Jackson deserialization
    }

    public User(String username, String email, String encodedPassword, String role, String phoneNumber) {
        // Constructor for convenience
        this.username = username;
        this.email = email;
        this.password = encodedPassword;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
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

    public List<Book> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<Book> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    public String getResetPwdToken() {
        return resetPwdToken;
    }

    public void setResetPwdToken(String resetPwdToken) {
        this.resetPwdToken = resetPwdToken;
    }

    public LocalDateTime getResetPwdExpirationTime() {
        return resetPwdExpirationTime;
    }

    public void setResetPwdExpirationTime(LocalDateTime resetPwdExpirationTime) {
        this.resetPwdExpirationTime = resetPwdExpirationTime;
    }
}