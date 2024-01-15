package com.whiteboard.kobo.model;

public class Creator {
    private String creatorId;
    private String creatorName;

    public Creator(String id, String name) {
        this.creatorId = id;
        this.creatorName = name;
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
