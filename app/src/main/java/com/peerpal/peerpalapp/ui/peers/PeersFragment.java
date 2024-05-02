package com.peerpal.peerpalapp.ui.peers;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    // RecyclerView to display peers
    RecyclerView recyclerView;
    // List of peers
    ArrayList<PeersClass> peersList;
    // List of connections
    ArrayList<String> connectionList;
    // Instance of PeersClass
    PeersClass peersClass;
    // Adapter for RecyclerView
    PeersAdapter adapter;
    // UID of the current user
    String peersUID;
    // List of hobbies of the current user
    ArrayList<String> selfHobbies;

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
        // Firebase authentication instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        // Get UID of the current user
        peersUID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Retrieve hobbies of the current user from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("peers").document(peersUID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                selfHobbies = (ArrayList<String>)document.get("hobbies");
            }
        });

        // Setup RecyclerView
        setupRecyclerView();
        return view;
    }

    // Method to setup RecyclerView
    void setupRecyclerView() {
        // Create GridLayoutManager for RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PeersFragment.this.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Retrieve connections of the current user from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("peers").document(peersUID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                connectionList = ((ArrayList<String>)document.get("connections"));
            } else {
                Log.d(TAG, "Error getting user document: ", task.getException());
            }
        });

        // Retrieve peers from Firestore
        db.collection("peers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String,Object> documentData = document.getData();
                            boolean connectionExists = false;

                            for (String connection : connectionList) {
                                if (Objects.requireNonNull(documentData.get("uid")).toString().equals(connection)) {
                                    connectionExists = true;
                                }
                            }

                            // Add peer to the list if it's not the current user and not in connection list
                            if ((!Objects.requireNonNull(documentData.get("uid")).toString().equals(peersUID)) && (!connectionExists)) {
                                String[] peersHobbies = new String[]{"", "", ""};

                                for (int i = 0; i < ((ArrayList<String>) Objects.requireNonNull(documentData.get("hobbies"))).size(); i++) {
                                    peersHobbies[i] = ((ArrayList<String>) Objects.requireNonNull(documentData.get("hobbies"))).get(i);
                                }

                                peersClass = new PeersClass(Objects.requireNonNull(documentData.get("uid")).toString(), Objects.requireNonNull(documentData.get("name")).toString(), Objects.requireNonNull(documentData.get("degree")).toString(), peersHobbies, Objects.requireNonNull(documentData.get("image")).toString());
                                peersList.add(peersClass);
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                    if (!(peersList == null)) {
                        // Sort peersList based on selfHobbies
                        peersList.sort(new HobbiesComparator(selfHobbies));
                        // Initialize and set adapter for RecyclerView
                        adapter = new PeersAdapter(PeersFragment.this.getContext(), peersList);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}

// Comparator class to compare peers based on the count of matching hobbies with the current user
class HobbiesComparator implements Comparator<PeersClass> {
    // List of hobbies of the current user
    private final ArrayList<String> hobbyList;

    // Constructor to initialize hobbyList
    HobbiesComparator(ArrayList<String> hobbyList) {
        this.hobbyList = hobbyList;
    }

    // Compare method to compare two PeersClass objects based on the count of matching hobbies with the current user
    @Override
    public int compare(PeersClass p1, PeersClass p2) {
        // Get the count of matching hobbies for each peer
        int p1Count = hobbyCount(p1);
        int p2Count = hobbyCount(p2);

        // Compare peers based on hobby counts in descending order
        return p2Count - p1Count;
    }

    // Method to count the number of matching hobbies between a peer and the current user
    int hobbyCount(PeersClass p) {
        // Initialize the total count of matching hobbies
        int totalCount = 0;

        // Loop through the hobbies of the current user
        for (int i = 0; i < hobbyList.size(); i++) {
            // Loop through the hobbies of the peer
            for (String peerHobby : p.getPeersHobbies()) {
                // If the hobby of the peer matches a hobby of the current user, increment the total count
                if (hobbyList.get(i).equals(peerHobby)) {
                    totalCount++;
                }
            }
        }

        // Return the total count of matching hobbies
        return totalCount;
    }
}