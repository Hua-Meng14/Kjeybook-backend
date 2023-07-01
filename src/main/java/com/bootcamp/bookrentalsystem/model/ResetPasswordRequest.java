package com.bootcamp.bookrentalsystem.model;

import java.util.UUID;

public class ResetPasswordRequest {

    private UUID userId;
    private String newPassword;
    private String resetPwdToken;

    public ResetPasswordRequest() {

    }

    public ResetPasswordRequest(UUID userId, String newPassword, String resetPwdToken) {
        this.userId = userId;
        this.newPassword = newPassword;
        this.resetPwdToken = resetPwdToken;
    }

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getResetPwdToken() {
        return resetPwdToken;
    }
    public void setResetPwdToken(String resetPwdToken) {
        this.resetPwdToken = resetPwdToken;
    }
}
