package com.whiteboard.kobo.model;

import java.util.Date;

public class Message {
    private String content;
    private String senderId;
    private String senderName;
    Date timestamp;
    private boolean isMe;

    public Message() {
    }

    public Message(String message, String senderId, String senderName, Date timestamp, boolean isMe) {
        this.content = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.isMe = isMe;
    }

    public String getMessage() {
        return content;
    }

    public void setMessage(String message) {
        this.content = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isMe() {
        return senderId.equals(UserData.getInstance().getId());
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
