package com.example.healthi.ai

public class Message {
    private long id;
    private String messageText;
    private boolean isUser;
    private String timestamp;

    public Message() {
    }

    public Message(String messageText, boolean isUser) {
        this.messageText = messageText;
        this.isUser = isUser;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}