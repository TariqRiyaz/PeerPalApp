package com.peerpal.peerpalapp.ui.messages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.peerpal.peerpalapp.R;
import com.peerpal.peerpalapp.databinding.FragmentMessagesBinding;

// Fragment for messages
public class MessagesFragment extends Fragment {

    private FragmentMessagesBinding binding;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.messageRecyclerView);

        // Setup RecyclerView
        setupRecyclerView();

        return view;
    }

    void setupRecyclerView(){
        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUserId = user.getUid();

        // Query to retrieve all chatrooms where the current user is a member
        Query query = allChatroomCollectionReference().whereArrayContains("userIds",currentUserId);
        Log.d("query", query.toString());

        // Configure options for the FirestoreRecyclerAdapter
        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class).build();

        // Initialize and set up the adapter
        adapter = new RecentChatRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // Start listening for changes when the fragment starts
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    // Stop listening for changes when the fragment stops
    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    // Notify the adapter when the fragment resumes
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

    // Clean up when the view is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Method to get reference to the "chatrooms" collection
    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
}