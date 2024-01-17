package com.whiteboard.kobo.model;

import com.google.gson.annotations.SerializedName;

public class Deletion {
    @SerializedName("boardId")
    private String boardId;

    public Deletion() {
    }

    public Deletion(String boardId) {
        this.boardId = boardId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }
}
