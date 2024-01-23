package com.whiteboard.kobo.model;

import java.util.Date;

public class Message {
    private String message;
    private String sender;
    Date timestamp;
    private boolean isMe;

    public Message() {
    }

    public Message(String message, String sender, Date timestamp, boolean isMe) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
