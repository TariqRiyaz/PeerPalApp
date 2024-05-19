package com.peerpal.peerpalapp.ui.peers;


// Model class to represent a peer
public class PeersClass {


    private  String peersUID;
    private  String peersName;
    private  String peersDegree;
    private  String[] peersHobbies;
    private  String peersImage;
    private String fcmToken; // User's FCM Token

    public PeersClass(){}

    // Constructor to initialize a PeersClass object
    public PeersClass(String peersUID, String peersName, String peersDegree, String[] peersHobbies, String peersImage) {
        this.peersUID = peersUID;
        this.peersName = peersName;
        this.peersDegree = peersDegree;
        this.peersHobbies = peersHobbies;
        this.peersImage = peersImage;
    }


    // Getter methods for accessing private member variables
    public String getPeersUID() {
        return peersUID;
    }


    public String getPeersName() {
        return peersName;
    }



    public String getPeersDegree() {
        return peersDegree;
    }

    public String[] getPeersHobbies() {
        return peersHobbies;
    }

    public String getPeersImage() {
        return peersImage;
    }

    // Getter for FcmToken
    public String getFcmToken() {
        return fcmToken;
    }

    public void setPeersUID(String peersUID) {
        this.peersUID = peersUID;
    }

    public void setPeersName(String peersName) {
        this.peersName = peersName;
    }

    public void setPeersDegree(String peersDegree) {
        this.peersDegree = peersDegree;
    }

    public void setPeersHobbies(String[] peersHobbies) {
        this.peersHobbies = peersHobbies;
    }

    public void setPeersImage(String peersImage) {
        this.peersImage = peersImage;
    }


    // Setter for FcmToken
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }



}