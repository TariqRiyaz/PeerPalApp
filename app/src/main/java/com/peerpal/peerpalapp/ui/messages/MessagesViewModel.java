package com.peerpal.peerpalapp.ui.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// Model for messages view
public class MessagesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    // Constructor to initialize the MutableLiveData
    public MessagesViewModel() {
        mText = new MutableLiveData<>();
        // Set initial value for the text
        mText.setValue("This is messages fragment");
    }

    // Method to get the LiveData object containing the text data
    public LiveData<String> getText() {
        return mText;
    }
}