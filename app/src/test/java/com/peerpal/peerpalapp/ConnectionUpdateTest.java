package com.peerpal.peerpalapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class ConnectionUpdateTest {
    boolean showChatroom = false;

    @Test
    public void testConnectionDeletion() {
        ArrayList<String> currentConnectionList = new ArrayList<>();
        currentConnectionList.add("abc");
        currentConnectionList.add("bcd");
        currentConnectionList.add("dce");

        String peerConnection = "abc";

        currentConnectionList.forEach(connection -> {
            if (connection.equals(peerConnection)) {
                addChatroomTest();
            }
        });

        assertEquals(true, showChatroom);
    }

    public void addChatroomTest() {
        this.showChatroom = true;
    }
}