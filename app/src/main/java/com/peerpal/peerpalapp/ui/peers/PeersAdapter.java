package com.peerpal.peerpalapp.ui.peers;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.peerpal.peerpalapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// Adapter class for the RecyclerView to bind data to the views
public class PeersAdapter extends RecyclerView.Adapter<PeersViewHolder> {
    private final Context context;
    private final ArrayList<PeersClass> peersList;
    public PeersAdapter(Context context, ArrayList<PeersClass> peersList){
        this.context = context;
        this.peersList = peersList;
    }

    @NonNull
    @Override
    public PeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peers_item, parent, false);
        // Creating a new PeersViewHolder with the inflated view
        return new PeersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PeersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Extracting the first hobby from the peers' hobbies array
        StringBuilder hobbyString = getStringBuilder(position);

        // Loading peer's image into the ImageView using Picasso
        Picasso.get().load(peersList.get(position).getPeersImage()).into(holder.peersImage);
        // Setting peer's name
        holder.peersName.setText(peersList.get(position).getPeersName());
        // Setting peer's degree
        holder.peersDegree.setText("Degree: " + peersList.get(position).getPeersDegree());
        // Setting peer's hobbies
        holder.peersHobbies.setText("Hobbies: " + hobbyString);

        // Setting onClickListener for connecting with the peer
        holder.peersConnect.setOnClickListener(v -> {
            // Initiating connection with the peer
            connectPeers(peersList.get(position).getPeersUID(), position);
        });
    }

    @NonNull
    private StringBuilder getStringBuilder(int position) {
        StringBuilder hobbyString = new StringBuilder(peersList.get(position).getPeersHobbies()[0]);

        // Concatenating non-empty hobbies into the hobbyString
        for (int i = 1; i < peersList.get(position).getPeersHobbies().length; i++) {
            if (!peersList.get(position).getPeersHobbies()[i].isEmpty()) {
                hobbyString.append(", ").append(peersList.get(position).getPeersHobbies()[i]);
            }
        }
        return hobbyString;
    }

    @Override
    public int getItemCount() {
        return peersList.size();
    }

    public void connectPeers(String peerUID, int position) {
        // Get the current user's UID
        String selfUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Reference to the current user's document
        DocumentReference selfDocRef = db.collection("peers").document(selfUID);

        // Fetch the current user's document
        selfDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                // Check if the peerUID is not already in the current user's connections list
                if (!((ArrayList<String>) Objects.requireNonNull(document.get("connections"))).contains(peerUID)) {
                    // If not, add the peerUID to the connections list
                    ArrayList<String> selfArray = (ArrayList<String>) document.get("connections");
                    assert selfArray != null;
                    selfArray.add(peerUID);
                    // Update the connections list in the current user's document
                    selfDocRef.update("connections", selfArray);
                }

            } else {
                // Log an error if there's an issue getting the user document
                Log.d(TAG, "Error getting user document: ", task.getException());
            }
        });

        // Check to create a chatroom
        db.collection("peers").get().addOnCompleteListener(task -> {
            boolean selfUIDCheck = false;

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> documentData = document.getData();
                    // Check if the document is not the current user's document
                    if (!Objects.requireNonNull(documentData.get("uid")).toString().equals(selfUID)) {
                        // Check if the current user and the peer are connected
                        if (((ArrayList<String>) Objects.requireNonNull(documentData.get("connections"))).contains(selfUID) && (Objects.equals(documentData.get("uid"), peerUID))) {
                            selfUIDCheck = true;
                        }
                    }
                }
            } else {
                // Log an error if there's an issue getting documents
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
            if (selfUIDCheck) {
                // If the current user and the peer are connected, check if a chatroom already exists
                db.collection("chatrooms").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        String potentialIdentifier1 = (peerUID + "_" + selfUID);
                        String potentialIdentifier2 = (selfUID + "_" + peerUID);
                        boolean chatroomExist = false;

                        // Loop through the existing chatrooms to check if a chatroom already exists between the current user and the peer
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            if ((Objects.equals(document.get("chatRoomId"), potentialIdentifier1)) || (Objects.equals(document.get("chatRoomId"), potentialIdentifier2))) {
                                chatroomExist = true;
                            }
                        }
                        // If a chatroom doesn't exist, create a new chatroom
                        if (!chatroomExist) {
                            String customUID;
                            Map<String, Object> chatroom = new HashMap<>();

                            // Determine the customUID for the chatroom
                            if (selfUID.hashCode() < peerUID.hashCode()) {
                                customUID = (selfUID + "_" + peerUID);
                                chatroom.put("chatRoomId", (selfUID + "_" + peerUID));
                            } else {
                                customUID = (peerUID + "_" + selfUID);
                                chatroom.put("chatRoomId", (peerUID + "_" + selfUID));
                            }

                            // Set initial values for the chatroom
                            chatroom.put("lastMessage", "");
                            chatroom.put("lastMessageSenderId", "");
                            chatroom.put("lastMessageTimeStamp", Timestamp.now());
                            ArrayList<String> userIds = new ArrayList<>();
                            userIds.add(selfUID);
                            userIds.add(peerUID);
                            chatroom.put("userIds", userIds);

                            // Add the chatroom to the Firestore database
                            DocumentReference docRef = db.collection("chatrooms").document(customUID);
                            docRef.set(chatroom);
                            // Show a toast indicating that the chatroom is opened with the peer
                            Toast.makeText(context.getApplicationContext(), ("CHATROOM OPEN WITH: " + peerUID), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Log an error if there's an issue getting documents
                        Log.d(TAG, "Error getting documents: ", task1.getException());
                    }
                });
            }
        });
        // Remove the peer from the peersList and notify the adapter
        peersList.remove(position);
        this.notifyItemRemoved(position);
    }
}

// ViewHolder class for the RecyclerView adapter, holding views for each item
class PeersViewHolder extends RecyclerView.ViewHolder {
    // Views to be held in the ViewHolder
    ImageView peersImage;
    TextView peersName, peersDegree, peersHobbies;
    CardView peersCard;
    Button peersConnect;

    // Constructor to initialize the views
    public PeersViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize views by finding them in the item layout
        peersImage = itemView.findViewById(R.id.peersImage);
        peersName = itemView.findViewById(R.id.peersName);
        peersDegree = itemView.findViewById(R.id.peersDegree);
        peersHobbies = itemView.findViewById(R.id.peersHobbies);
        peersCard = itemView.findViewById(R.id.peersCard);
        peersConnect = itemView.findViewById((R.id.peersConnect));
    }
}