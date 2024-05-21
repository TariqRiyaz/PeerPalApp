package com.peerpal.peerpalapp.ui.messages;

import com.google.firebase.Timestamp;

// Model class for chat message
public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;

    // Default constructor (required by Firestore)
    public ChatMessageModel(){
    }

    // Parameterized constructor
    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter for senderId
    public String getSenderId() {
        return senderId;
    }

    // Setter for senderId
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    // Getter for timestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setter for timestamp
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}