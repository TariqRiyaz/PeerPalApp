package com.peerpal.peerpalapp.ui.messages;

import android.media.Image;
import android.widget.ImageButton;

import com.google.firebase.Timestamp;

import java.util.List;

// Model for chatroom
public class ChatRoomModel {
    String chatRoomId;
    List<String> userIds;
    Timestamp lastMessageTimeStamp;
    String lastMessageSenderId;
    String lastMessage;
    ImageButton deleteConnectionButton;

    boolean isPinned;

    // Default constructor (required by Firestore)
    public ChatRoomModel() {
    }

    // Parameterized constructor
    public ChatRoomModel(String chatRoomId, List<String> userIds, Timestamp lastMessageTimeStamp, String lastMessageSenderId, ImageButton deleteConnectionButton, boolean isPinned) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.deleteConnectionButton = deleteConnectionButton;
        this.isPinned = isPinned;
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

    // Getter for deleteConnectionButton
    public ImageButton getDeleteConnectionButton() {
        return deleteConnectionButton;
    }

    // Setter for deleteConnectionButton
    public void setDeleteConnectionButton(ImageButton deleteConnectionButton) {
        this.deleteConnectionButton = deleteConnectionButton;
    }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }
}