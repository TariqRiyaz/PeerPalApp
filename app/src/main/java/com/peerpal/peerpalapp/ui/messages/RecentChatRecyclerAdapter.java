package com.peerpal.peerpalapp.ui.messages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.peerpal.peerpalapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Adapter for recent chat recycler
public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    Context context;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    MessagesFragment fragment;

    // Constructor
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context, MessagesFragment fragment) {
        super(options);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        // Initialize views
        holder.usernameText.setText(""); // Clear previous text
        holder.pinChatButtonValue.setImageResource(model.getIsPinned() == 1 ? R.drawable.pin_filled_icon : R.drawable.pin_hollow_icon);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(user).getUid();
        String chatroomId = model.getChatRoomId();

        ArrayList<String> allUID = new ArrayList<>(model.getUserIds()); // Copying user IDs to an ArrayList

        // Get the other user's information from Firestore
        getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                MessageUserModel otherUserModel = task.getResult().toObject(MessageUserModel.class);
                if (otherUserModel != null) {
                    String otherUserImage = otherUserModel.getImage();
                    if (!otherUserImage.isEmpty()) {
                        Picasso.get().load(otherUserImage).into(holder.profilePic);
                    }
                    holder.usernameText.setText(otherUserModel.getName());
                    // Add other user's name and image URL to the list for passing to ChatActivity
                    allUID.add(otherUserModel.getName());
                    allUID.add(otherUserModel.getImage());
                    allUID.add(otherUserModel.getPhone());
                } else {
                    Log.e("RecentChatRecyclerAdapter", "Other user model is null");
                }

                // Set click listener to navigate to ChatActivity
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("selfUID", allUID.get(0));
                    intent.putExtra("peerUID", allUID.get(1));
                    intent.putExtra("peerName", allUID.get(2));
                    intent.putExtra("peerImage", allUID.get(3));
                    intent.putExtra("peerPhone", allUID.get(4));
                    context.startActivity(intent);
                });
            }
        });

        // Set click listener for pinning/unpinning chats
        holder.pinChatButtonValue.setOnClickListener(v -> {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("chatrooms").document(model.getChatRoomId());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Get the current pin state
                    Long pinValue = documentSnapshot.getLong("isPinned");
                    if (pinValue != null) {
                        // Toggle the pin state
                        int newPinValue = pinValue == 1 ? 0 : 1;
                        // Update Firestore with the new pin state
                        docRef.update("isPinned", newPinValue)
                                .addOnSuccessListener(aVoid -> {
                                    // Update the UI based on the new pin state
                                    updatePinIcon(holder.pinChatButtonValue, newPinValue);
                                    Toast.makeText(context, "Chat " + (newPinValue == 1 ? "pinned" : "unpinned"), Toast.LENGTH_SHORT).show();
                                    fragment.onChatroomUpdated();
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update pin state", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });

        // Set click listener for deleting chats
        holder.deleteConnectionButton.setOnClickListener(v -> {
            // Alerts user to confirm deletion of connection
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Confirmation");
            builder.setMessage("Are you sure you want to delete connection?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Removing peer connection from connections list in Firestore
                    String existingUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference peerRef = db.collection("peers").document(existingUser);
                    peerRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                ArrayList<String> connectionArray = (ArrayList<String>) doc.get("connections");
                                if (connectionArray != null) {
                                    connectionArray.remove(model.getUserIds().get(1)); // Remove the other user from connections
                                    peerRef.update("connections", connectionArray)
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Connection removed successfully"))
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error removing connection", e));
                                    fragment.onChatroomUpdated();
                                }
                            }
                        }
                    });

                    // Removing chatroom from chatrooms collection
                    db.collection("chatrooms").document(chatroomId).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Chat deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete chat", Toast.LENGTH_SHORT).show());
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void updatePinIcon(ImageButton pinButton, int pinValue) {
        if (pinValue == 1) {
            pinButton.setImageResource(R.drawable.pin_filled_icon);
        } else {
            pinButton.setImageResource(R.drawable.pin_hollow_icon);
        }
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    // Method to get other user's profile picture URL
    public String getOtherProfilePicStorageRef(MessageUserModel userModel){
        return userModel.getImage();
    }

    // Method to convert timestamp to string
    @SuppressLint("SimpleDateFormat")
    public String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    // ViewHolder class
    public static class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        ImageButton deleteConnectionButton;
        ImageButton pinChatButtonValue;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            deleteConnectionButton = itemView.findViewById(R.id.deleteConnectionButton);
            pinChatButtonValue = itemView.findViewById(R.id.pinChatButton);
        }
    }

    // Method to get a DocumentReference to the other user in the chat room
    private DocumentReference getOtherUserFromChatroom(List<String> userIds){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        currentUserId = user.getUid();

        // Check which user ID is not the current user's ID
        String otherUserId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);
        Log.d("otherUserId", otherUserId);

        // Return DocumentReference to the other user
        return allUserCollectionReference().document(otherUserId);
    }

    // Method to get reference to the "peers" collection
    private CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("peers");
    }
}
