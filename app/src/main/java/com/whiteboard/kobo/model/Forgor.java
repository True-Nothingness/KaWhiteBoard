package com.whiteboard.kobo.model;

import com.google.gson.annotations.SerializedName;

public class Forgor {
    @SerializedName("email")
    private String email;

    public Forgor() {
    }
    public Forgor(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
