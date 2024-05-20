package com.peerpal.peerpalapp;

import static org.junit.Assert.assertEquals;

import com.peerpal.peerpalapp.ui.peers.PeersClass;
import com.peerpal.peerpalapp.ui.peers.PeersFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PeerConnectionTest {
    boolean showUserInView = true;

    @Test
    public void testPeerConnection() {
        String userToSuggest = "abc";

        ArrayList<String> recommendedPeersList = new ArrayList<>();
        recommendedPeersList.add("123");
        recommendedPeersList.add("234");

        ArrayList<String> connectionList = new ArrayList<>();
        connectionList.add("abc");
        connectionList.add("bcd");

        if (connectionList.contains(userToSuggest)) {
            showUserInView = false;
        } else {
            recommendedPeersList.add(userToSuggest);
        }

        assertEquals(false, recommendedPeersList.contains(userToSuggest));
        assertEquals(false, showUserInView);
    }
}