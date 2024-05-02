package com.peerpal.peerpalapp.ui.messages;

import com.google.firebase.Timestamp;

import java.util.List;

// Model for chatroom
public class ChatRoomModel {

    // Declare variables
    String chatRoomId; // Unique ID for the chat room
    List<String> userIds; // IDs of users in the chat room
    Timestamp lastMessageTimeStamp; // Timestamp of the last message in the chat room
    String lastMessageSenderId; // ID of the user who sent the last message
    String lastMessage; // Content of the last message

    // Default constructor (required by Firestore)
    public ChatRoomModel() {
    }

    // Parameterized constructor
    public ChatRoomModel(String chatRoomId, List<String> userIds, Timestamp lastMessageTimeStamp, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    // Getter for lastMessage
    public String getLastMessage() {
        return lastMessage;
    }

    // Setter for lastMessage
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    // Getter for chatRoomId
    public String getChatRoomId() {
        return chatRoomId;
    }

    // Setter for chatRoomId
    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    // Getter for userIds
    public List<String> getUserIds() {
        return userIds;
    }

    // Setter for userIds
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    // Getter for lastMessageTimeStamp
    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    // Setter for lastMessageTimeStamp
    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    // Getter for lastMessageSenderId
    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    // Setter for lastMessageSenderId
    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}