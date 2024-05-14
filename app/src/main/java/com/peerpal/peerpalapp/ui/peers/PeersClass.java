package com.peerpal.peerpalapp.ui.peers;


// Model class to represent a peer
public class PeersClass {
    private final String peersUID;
    private final String peersName;
    private final String peersDegree;
    private final String[] peersHobbies;
    private final String peersImage;
    private final String peersPhone;

    // Constructor to initialize a PeersClass object
    public PeersClass(String peersUID, String peersName, String peersDegree, String[] peersHobbies, String peersImage, String peersPhone) {
        this.peersUID = peersUID;
        this.peersName = peersName;
        this.peersDegree = peersDegree;
        this.peersHobbies = peersHobbies;
        this.peersImage = peersImage;
        this.peersPhone = peersPhone;
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

    public String getPeersPhone() {
        return peersPhone;
    }
}