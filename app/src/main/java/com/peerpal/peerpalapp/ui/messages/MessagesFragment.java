package com.peerpal.peerpalapp.ui.messages;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.peerpal.peerpalapp.R;
import com.peerpal.peerpalapp.databinding.FragmentMessagesBinding;
import com.peerpal.peerpalapp.ui.peers.PeersAdapter;
import com.peerpal.peerpalapp.ui.peers.PeersClass;
import com.peerpal.peerpalapp.ui.peers.PeersFragment;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

// Fragment for messages
public class MessagesFragment extends Fragment {
    private FragmentMessagesBinding binding;
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    TextView messagePlaceholder;
    ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUserId = user.getUid();

        // Initialize views
        recyclerView = view.findViewById(R.id.messageRecyclerView);
        messagePlaceholder = view.findViewById(R.id.messagesPlaceholder);
        progressBar = view.findViewById(R.id.progressBar);
        showLoading(true);
        checkChatrooms();

        return view;
    }

    void checkChatrooms() {
        db.collection("chatrooms").whereArrayContains("userIds", currentUserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().getDocuments().isEmpty()) {
                        setupRecyclerView();
                    } else {
                        setupTextView();
                    }
                } else {
                    setupTextView();
                }
            }
        });
    }

    void setupRecyclerView(){
        // Query to retrieve all chatrooms where the current user is a member
        Query query = allChatroomCollectionReference().whereArrayContains("userIds",currentUserId);

        // Configure options for the FirestoreRecyclerAdapter
        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class).build();

        // Initialize and set up the adapter
        showLoading(false);
        adapter = new RecentChatRecyclerAdapter(options, getContext());
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    void setupTextView(){
        showLoading(false);
        messagePlaceholder.setVisibility(View.VISIBLE);
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

    // Function to toggle loading wheel on/off
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}