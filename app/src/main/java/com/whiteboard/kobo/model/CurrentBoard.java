package com.whiteboard.kobo.model;

import java.util.Date;
import java.util.List;

public class CurrentBoard {
    private static CurrentBoard instance;
    private Creator creator;
    private String _id;
    private String name;
    private Date timestamp;
    private List<UserResponse> users;
    private List<Drawing> drawings;
    private int __v;
    private CurrentBoard() {
    }

    public static synchronized CurrentBoard getInstance() {
        if (instance == null) {
            instance = new CurrentBoard();
        }
        return instance;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getBoardName() {
        return name;
    }

    public void setBoardName(String boardName) {
        this.name = boardName;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }

    public List<Drawing> getDrawings() {
        return drawings;
    }

    public void setDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
    }

    public int getVersion() {
        return __v;
    }

    public void setVersion(int version) {
        this.__v = version;
    }
}
