package com.whiteboard.kobo.model;

public class Request {
    private String boardId;
    private String userId;
    private String userName;

    public Request() {
    }

    public Request(String boardId, String userId, String userName) {
        this.boardId = boardId;
        this.userId = userId;
        this.userName = userName;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
