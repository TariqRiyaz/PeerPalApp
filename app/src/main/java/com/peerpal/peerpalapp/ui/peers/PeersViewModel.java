package com.peerpal.peerpalapp.ui.peers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

public class PeersViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    RecyclerView.Recycler peersView;

    public PeersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is peers fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}