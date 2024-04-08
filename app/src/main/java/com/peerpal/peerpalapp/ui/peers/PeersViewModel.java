package com.peerpal.peerpalapp.ui.peers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PeersViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PeersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is peers fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}