package com.peerpal.peerpalapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class AccountVerificationTest {
    boolean correctEmailFormat = false;
    boolean correctOTP = false;

    @Test
    public void testAccountVerification() {
        String userInputEmail = "abc1234@autuni.ac.nz";
        String userInputOTP = "123456";

        if (userInputEmail.endsWith("@autuni.ac.nz")) {
            correctEmailFormat = true;
        }

        if (userInputOTP.equals("123456")) {
            correctOTP = true;
        }

        if (correctEmailFormat && correctOTP) {
            createAccount();
        }

        assertEquals(true, correctEmailFormat);
        assertEquals(true, correctOTP);
    }

    public void createAccount() {
        // Test Create Account Function
    }
}