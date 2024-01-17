package com.whiteboard.kobo.model;

public class UserResponse {
    private String _id;
    private String name;
    private String role;

    public UserResponse() {
    }

    public UserResponse(String id, String name, String role) {
        this._id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
