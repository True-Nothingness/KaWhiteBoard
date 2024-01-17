package com.whiteboard.kobo.model;

public class BoardJSON {
    private String boardName;
    private String creatorId;
    private String creatorName;

    public BoardJSON() {
    }

    public BoardJSON(String boardName, String creatorId, String creatorName) {
        this.boardName = boardName;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
