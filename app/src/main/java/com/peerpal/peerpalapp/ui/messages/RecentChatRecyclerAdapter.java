package com.peerpal.peerpalapp.ui.messages;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.peerpal.peerpalapp.ProfileEdit;
import com.peerpal.peerpalapp.R;
import com.peerpal.peerpalapp.SplashScreen;
import com.peerpal.peerpalapp.ViewProfile;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

// Adapter for recentchatrecycler
public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    Context context;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    String chatroomId;
    ImageButton deleteConnectionButton;


    // Constructor
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        chatroomId = model.getChatRoomId();
        deleteConnectionButton = holder.deleteConnectionButton;

        ArrayList<String> allUID = new ArrayList<>(model.getUserIds()); // Copying user IDs to an ArrayList

        // Get the other user's information from Firestore
        getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                MessageUserModel otherUserModel = task.getResult().toObject(MessageUserModel.class);
                if (otherUserModel != null) {
                    currentUserId = otherUserModel.getUid();
                    String OtheruserImage = otherUserModel.getImage();
                    if (!OtheruserImage.isEmpty()) {
                        Picasso.get().load(OtheruserImage).into(holder.profilePic);
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
                // Set click listener for pinning/unpinning chats
                holder.pinChatButtonValue.setOnClickListener(v -> {
                    boolean isPinned = !model.isPinned(); // Toggle pin state
                    FirebaseFirestore.getInstance().collection("chatrooms")
                            .document(model.getChatRoomId())
                            .update("isPinned", isPinned)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(context, isPinned ? "Chat pinned" : "Chat unpinned", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "Failed to update pin state", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
            }
        });
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUserId = user.getUid();

        // Check which user ID is not the current user's ID
        String otherUserId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);
        Log.d("otherUserId", otherUserId);

        // Sets button to delete user connection and chatroom
        deleteConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alerts user to confirm deletion of connection
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete connection?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Removing peer connection from connections list in Firestore
                        String existingUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference peerRef = db.collection("peers").document(existingUser);
                        peerRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                ArrayList<String> connectionArray = ((ArrayList<String>) Objects.requireNonNull(doc.get("connections")));
                                connectionArray.remove(otherUserId);
                                peerRef.update("connections", connectionArray);
                            }
                        });

                        //Removing chatroom from chatrooms collection
                        DocumentReference chatroomRef = db.collection("chatrooms").document(chatroomId);
                        chatroomRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                chatroomRef.delete();
                            }
                        });

                        //Updates chatroom recycler view
                        notifyDataSetChanged();
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
            }
        });

        // Return DocumentReference to the other user
        return allUserCollectionReference().document(otherUserId);
    }

    // Method to get reference to the "peers" collection
    private CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("peers");
    }
}