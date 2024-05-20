package com.peerpal.peerpalapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class AccountDeletionTest {

    @Mock
    FirebaseAuth mockFirebaseAuth;

    @Mock
    FirebaseUser mockUser;

    @Test
    public void testAccountDeletionConfirmation() {
        MockitoAnnotations.initMocks(this);

        when(mockUser.getEmail()).thenReturn("pmn0071@autuni.ac.nz");
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockUser);

        FirebaseUser currentUser = mockFirebaseAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail().equals("pmn0071@autuni.ac.nz")) {
            mockFirebaseAuth.getCurrentUser().delete();
            mockFirebaseAuth.signOut();
        }

        verify(mockFirebaseAuth.getCurrentUser()).delete();
        verify(mockFirebaseAuth).signOut();
    }
}