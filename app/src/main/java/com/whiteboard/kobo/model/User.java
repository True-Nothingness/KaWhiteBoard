package com.whiteboard.kobo.model;

public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private String userPwd;

    public User(String userID, String userName, String userEmail, String userPwd) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPwd = userPwd;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
