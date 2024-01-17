package com.whiteboard.kobo.model;

import com.google.gson.annotations.SerializedName;

public class PwdReset {
    @SerializedName("userId")
    private String userId;
    @SerializedName("token")
    private String token;
    @SerializedName("password")
    private String password;

    public PwdReset() {
    }

    public PwdReset(String userId, String token, String password) {
        this.userId = userId;
        this.token = token;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
