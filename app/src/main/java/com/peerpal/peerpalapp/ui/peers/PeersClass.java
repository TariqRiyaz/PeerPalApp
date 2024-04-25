package com.peerpal.peerpalapp.ui.peers;


import java.lang.reflect.Array;

public class PeersClass {
    private String peersUID;
    private String peersName;
    private String peersDegree;
    private String[] peersHobbies;
    private int peersImage;

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

    public int getPeersImage() {
        return peersImage;
    }

    public PeersClass(String peersUID, String peersName, String peersDegree, String[] peersHobbies, int peersImage) {
        this.peersUID = peersUID;
        this.peersName = peersName;
        this.peersDegree = peersDegree;
        this.peersHobbies = peersHobbies;
        this.peersImage = peersImage;
    }
}