package com.whiteboard.kobo.model;

public class Creator {
    private String _id;
    private String name;

    public Creator(String id, String name) {
        this._id = id;
        this.name = name;
    }

    public String getCreatorId() {
        return _id;
    }

    public void setCreatorId(String creatorId) {
        this._id = creatorId;
    }

    public String getCreatorName() {
        return name;
    }

    public void setCreatorName(String creatorName) {
        this.name = creatorName;
    }
}
