package com.peerpal.peerpalapp.ui.messages;

// Model for message user
public class MessageUserModel {
    private String name;
    private String email;
    private String image;
    private String uid;
    private String phone;

    // Default constructor (required by Firestore)
    public MessageUserModel() {
    }

    // Parameterized constructor
    public MessageUserModel(String name, String email, String image, String uid) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.uid = uid;
        this.phone = phone;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for image
    public String getImage() {
        return image;
    }

    // Setter for image
    public void setImage(String image) {
        this.image = image;
    }

    // Getter for uid
    public String getUid() {
        return uid;
    }

    // Setter for uid
    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter for name
    public String getPhone() {
        return phone;
    }

    // Setter for name
    public void getPhone(String phone) {
        this.phone = phone;
    }
}