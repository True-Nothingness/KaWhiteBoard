package com.whiteboard.kobo.model;

public class Drawing {
    private String drawingId;
    private String drawingContent;

    public Drawing(String drawingId, String drawingContent) {
        this.drawingId = drawingId;
        this.drawingContent = drawingContent;
    }

    public String getDrawingId() {
        return drawingId;
    }

    public String getDrawingContent() {
        return drawingContent;
    }
}
