package com.peerpal.peerpalapp.ui.peers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Comparators;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.peerpal.peerpalapp.R;
import com.peerpal.peerpalapp.databinding.FragmentPeersBinding;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class PeersFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<PeersClass> peersList;
    ArrayList<String> connectionList;
    PeersClass peersClass;
    PeersAdapter adapter;
    String peersUID;
    ArrayList<String> selfHobbies;
    private FirebaseAuth firebaseAuth;

    public PeersFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peers, container, false);
        peersList = new ArrayList<PeersClass>();
        connectionList = new ArrayList<String>();
        recyclerView = view.findViewById(R.id.recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        peersUID = firebaseAuth.getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("peers").document(peersUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    selfHobbies = (ArrayList<String>)document.get("hobbies");
                }
            }
        });

        setupRecyclerView();
        return view;
    }

    void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PeersFragment.this.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("peers").document(peersUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    connectionList = ((ArrayList<String>)document.get("connections"));
                } else {
                    Log.d(TAG, "Error getting user document: ", task.getException());
                }
            }
        });

        db.collection("peers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> documentData = document.getData();
                                boolean connectionExists = false;

                                for (String connection : connectionList) {
                                    if (documentData.get("uid").toString().equals(connection)) {
                                        connectionExists = true;
                                    }
                                }

                                if ((!documentData.get("uid").toString().equals(peersUID)) && (!connectionExists)) {
                                    String[] peersHobbies = new String[]{"", "", ""};

                                    for (int i = 0; i < ((ArrayList<String>)documentData.get("hobbies")).size(); i++) {
                                        peersHobbies[i] = ((ArrayList<String>)documentData.get("hobbies")).get(i);
                                    }

                                    peersClass = new PeersClass(documentData.get("uid").toString(), documentData.get("name").toString(), documentData.get("degree").toString(), peersHobbies, documentData.get("image").toString());
                                    peersList.add(peersClass);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        Collections.sort(peersList, new HobbiesComparator(selfHobbies));
                        adapter = new PeersAdapter(PeersFragment.this.getContext(), peersList);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}

class HobbiesComparator implements Comparator<PeersClass> {
    private ArrayList<String> hobbyList;
    HobbiesComparator(ArrayList<String> hobbyList) {
        this.hobbyList = hobbyList;
    }

    @Override
    public int compare(PeersClass p1, PeersClass p2) {
        int p1Count = hobbyCount(p1);
        int p2Count = hobbyCount(p2);

        return p2Count - p1Count;
    }

    int hobbyCount(PeersClass p) {
        int totalCount = 0;

        for (int i = 0; i < hobbyList.size(); i++) {
            for (String peerHobby : p.getPeersHobbies()) {
                if (hobbyList.get(i).equals(peerHobby)) {
                    totalCount++;
                }
            }
        }

        return totalCount;
    }
}