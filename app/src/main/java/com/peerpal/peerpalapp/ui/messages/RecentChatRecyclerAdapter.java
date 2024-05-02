package com.peerpal.peerpalapp.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.peerpal.peerpalapp.R;
import com.peerpal.peerpalapp.ui.messages.ChatActivity;
import com.peerpal.peerpalapp.ui.messages.ChatRoomModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.peerpal.peerpalapp.ui.messages.MessageUserModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    FirebaseAuth firebaseAuth;

    String currentUserId;
    String chatroomId;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        chatroomId = model.chatRoomId;
        ArrayList<String> allUID = (ArrayList<String>)model.getUserIds();

        getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        MessageUserModel otherUserModel = task.getResult().toObject(MessageUserModel.class);
                        Log.d("otherUserModel", otherUserModel.getName());
                        if (!otherUserModel.getUid().isEmpty()) {
                            currentUserId = otherUserModel.getUid();
                            Log.d("image", getOtherProfilePicStorageRef(otherUserModel));
                            String OtheruserImage = getOtherProfilePicStorageRef(otherUserModel);
                            if (!OtheruserImage.isEmpty()) {
                                Log.d("otherUserModel", OtheruserImage);
                                Picasso.get().load(OtheruserImage).into(holder.profilePic);
                            }
                            holder.usernameText.setText(otherUserModel.getName());
                            allUID.add(otherUserModel.getName());
                            allUID.add(otherUserModel.getImage());
                        } else {
                            Log.e("RecentChatRecyclerAdapter", "Other user model is null");
                        }
                            holder.itemView.setOnClickListener(v -> {
                                //navigate to chat activity
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("selfUID", allUID.get(0));
                                intent.putExtra("peerUID", allUID.get(1));
                                intent.putExtra("peerName", allUID.get(2));
                                intent.putExtra("peerImage", allUID.get(3));

                                context.startActivity(intent);
                            });

                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    public String  getOtherProfilePicStorageRef(MessageUserModel userModel){
        return userModel.getImage();
    }

    public String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
    private DocumentReference getOtherUserFromChatroom(List<String> userIds){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        if(userIds.get(0).equals(currentUserId)){
            Log.d("userIds", userIds.get(1));
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            Log.d("userIds", userIds.get(0));
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    private CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("peers");
    }
}