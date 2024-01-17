package com.whiteboard.kobo.model;

import java.util.Date;
import java.util.List;

public class Board {
    private Creator creator;
    private String _id;
    private String name;
    private Date timestamp;
    private List<UserResponse> users;
    private List<String> drawings;
    private int __v;
    public Board() {
    }

    public Board(Creator creator, String id, String boardName, Date timestamp, List<UserResponse> users, List<String> drawings, int version) {
        this.creator = creator;
        this._id = id;
        this.name = boardName;
        this.timestamp = timestamp;
        this.users = users;
        this.drawings = drawings;
        this.__v = version;
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

    public List<String> getDrawings() {
        return drawings;
    }

    public void setDrawings(List<String> drawings) {
        this.drawings = drawings;
    }

    public int getVersion() {
        return __v;
    }

    public void setVersion(int version) {
        this.__v = version;
    }
}
