package com.peerpal.peerpalapp.ui.peers;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.peerpal.peerpalapp.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

// Fragment for displaying a list of peers
public class PeersFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<PeersClass> peersList;
    ArrayList<String> connectionList;
    PeersClass peersClass;
    PeersAdapter adapter;
    String peersUID;
    ArrayList<String> selfHobbies = new ArrayList<>();
    ProgressBar progressBar;

    // Default constructor
    public PeersFragment() {
    }

    // Called to create the view hierarchy associated with the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_peers, container, false);
        // Initialize peersList and connectionList
        peersList = new ArrayList<>();
        connectionList = new ArrayList<>();
        // Find RecyclerView from layout
        recyclerView = view.findViewById(R.id.recyclerView);
        // Initialize Firebase authentication instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Get UID of the current user
        peersUID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        progressBar = view.findViewById(R.id.progressBar);
        showLoading(true);

        // Retrieve hobbies and connections of the current user from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("peers").document(peersUID);

        // Retrieve current user's hobbies and connections first
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    selfHobbies = (ArrayList<String>) document.get("hobbies");
                    connectionList = (ArrayList<String>) document.get("connections");
                    // Now retrieve all peers
                    retrievePeers();
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        setupRecyclerView();

        return view;
    }

    // Method to setup RecyclerView
    void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PeersFragment.this.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    // Method to retrieve peers from Firestore
    void retrievePeers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("peers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String,Object> documentData = document.getData();
                            boolean connectionExists = connectionList != null && connectionList.contains(documentData.get("uid").toString());

                            // Add peer to the list if it's not the current user and not in connection list
                            if (!Objects.requireNonNull(documentData.get("uid")).toString().equals(peersUID) && !connectionExists) {
                                String[] peersHobbies = new String[]{"", "", ""};

                                for (int i = 0; i < ((ArrayList<String>) Objects.requireNonNull(documentData.get("hobbies"))).size(); i++) {
                                    peersHobbies[i] = ((ArrayList<String>) Objects.requireNonNull(documentData.get("hobbies"))).get(i);
                                }

                                peersClass = new PeersClass(Objects.requireNonNull(documentData.get("uid")).toString(),
                                        Objects.requireNonNull(documentData.get("name")).toString(),
                                        Objects.requireNonNull(documentData.get("degree")).toString(),
                                        peersHobbies,
                                        Objects.requireNonNull(documentData.get("image")).toString(),
                                        Objects.requireNonNull(documentData.get("phone")).toString());
                                peersList.add(peersClass);
                            }
                        }

                        // Sort peersList based on selfHobbies
                        if (selfHobbies != null && peersList != null) {
                            peersList.sort(new HobbiesComparator(selfHobbies));
                            // Initialize and set adapter for RecyclerView
                            adapter = new PeersAdapter(PeersFragment.this.getContext(), peersList, selfHobbies);
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    // Hide loading indicator
                    showLoading(false);
                });
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

// Comparator class to compare peers based on the count of matching hobbies with the current user
class HobbiesComparator implements Comparator<PeersClass> {
    private final ArrayList<String> hobbyList;

    // Constructor to initialize hobbyList
    HobbiesComparator(ArrayList<String> hobbyList) {
        this.hobbyList = hobbyList;
    }

    // Compare method to compare two PeersClass objects based on the count of matching hobbies with the current user
    @Override
    public int compare(PeersClass p1, PeersClass p2) {
        int p1Count = hobbyCount(p1);
        int p2Count = hobbyCount(p2);
        return p2Count - p1Count;
    }

    // Method to count the number of matching hobbies between a peer and the current user
    int hobbyCount(PeersClass p) {
        int totalCount = 0;

        for (String hobby : hobbyList) {
            for (String peerHobby : p.getPeersHobbies()) {
                if (hobby.equals(peerHobby)) {
                    totalCount++;
                }
            }
        }
        return totalCount;
    }
}