package com.peerpal.peerpalapp.ui.peers;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.peerpal.peerpalapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class PeersAdapter extends RecyclerView.Adapter<PeersViewHolder> {
    private Context context;
    private ArrayList<PeersClass> peersList;
    public void setList(ArrayList<PeersClass> peersList){
        this.peersList = peersList;
    }
    public PeersAdapter(Context context, ArrayList<PeersClass> peersList){
        this.context = context;
        this.peersList = peersList;
    }
    @NonNull
    @Override
    public PeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peers_item, parent, false);
        return new PeersViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PeersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String hobbyString = peersList.get(position).getPeersHobbies()[0];

        for (int i = 1; i < peersList.get(position).getPeersHobbies().length; i++) {
            if (!peersList.get(position).getPeersHobbies()[i].equals("")) {
                hobbyString += (", " + peersList.get(position).getPeersHobbies()[i]);
            }
        }

        Picasso.get().load(peersList.get(position).getPeersImage()).into(holder.peersImage);
        holder.peersName.setText(peersList.get(position).getPeersName());
        holder.peersDegree.setText("Degree: " + peersList.get(position).getPeersDegree());
        holder.peersHobbies.setText("Hobbies: " + hobbyString);

        holder.peersConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectPeers(peersList.get(position).getPeersUID(), position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return peersList.size();
    }

    public void connectPeers(String peerUID, int position) {
        String selfUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference selfDocRef = db.collection("peers").document(selfUID);

        selfDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (!((ArrayList<String>)document.get("connections")).contains(peerUID)) {
                        ArrayList<String> selfArray = (ArrayList<String>)document.get("connections");
                        selfArray.add(peerUID);
                        selfDocRef.update("connections", selfArray);
                    }

                } else {
                    Log.d(TAG, "Error getting user document: ", task.getException());
                }
            }
        });

        //Check to create chatroom (Move to messages fragment)
        db.collection("peers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean selfUIDCheck = false;
                        boolean peerUIDCheck = false;

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> documentData = document.getData();
                                if (!documentData.get("uid").toString().equals(selfUID)) {
                                    if (((ArrayList<String>)documentData.get("connections")).contains(selfUID) && (documentData.get("uid").equals(peerUID))) {
                                        selfUIDCheck = true;
                                    }
                                } else {
                                    if (((ArrayList<String>)documentData.get("connections")).contains(peerUID) && (documentData.get("uid").equals(selfUID))) {
                                        peerUIDCheck = true;
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        if (selfUIDCheck && peerUIDCheck) {
                            //Opens chatroom
                            Toast.makeText(context.getApplicationContext(), ("CHATROOM OPEN WITH: " + peerUID), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        peersList.remove(position);
        this.notifyItemChanged(position);
    }
}

class PeersViewHolder extends RecyclerView.ViewHolder {
    ImageView peersImage;
    TextView peersName, peersDegree, peersHobbies;
    CardView peersCard;
    Button peersConnect;

    public PeersViewHolder(@NonNull View itemView) {
        super(itemView);

        peersImage = itemView.findViewById(R.id.peersImage);
        peersName = itemView.findViewById(R.id.peersName);
        peersDegree = itemView.findViewById(R.id.peersDegree);
        peersHobbies = itemView.findViewById(R.id.peersHobbies);
        peersCard = itemView.findViewById(R.id.peersCard);
        peersConnect = itemView.findViewById((R.id.peersConnect));
    }
}