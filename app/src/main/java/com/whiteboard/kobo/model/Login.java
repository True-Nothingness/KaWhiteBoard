package com.whiteboard.kobo.model;

public class Login {
    private String email;
    private String password;

    public Login() {
    }

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUserEmail() {
        return email;
    }

    public String getUserPwd() {
        return password;
    }

    public void setUserEmail(String userEmail) {
        this.email = email;
    }

    public void setUserPwd(String userPwd) {
        this.password = password;
    }
}
