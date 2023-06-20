package com.bootcamp.bookrentalsystem.model;

public class ResetPasswordRequest {

    private String email;
    private String newPassword;
    private String resetPwdToken;

    public ResetPasswordRequest() {

    }

    public ResetPasswordRequest(String email, String newPassword, String resetPwdToken) {
        this.email = email;
        this.newPassword = newPassword;
        this.resetPwdToken = resetPwdToken;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
