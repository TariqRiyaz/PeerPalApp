package com.peerpal.peerpalapp.ui.messages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.peerpal.peerpalapp.R;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {
    String chatRoomId;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendImageButton;
    ImageButton backBtn;
    ImageButton callBtn;
    TextView otherUsername;
    ImageView otherImage;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    String peerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the layout
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initialize views
        messageInput = findViewById(R.id.chat_message_input);
        sendImageButton = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        callBtn = findViewById(R.id.call_btn);
        otherUsername = findViewById(R.id.other_username);
        otherImage = findViewById(R.id.other_image);
        recyclerView = findViewById(R.id.chat_recycler_view);

        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        currentUserId = user.getUid();

        // Retrieve data from intent
        String selfUID;
        String peerUID;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selfUID = extras.getString("selfUID");
            peerUID = extras.getString("peerUID");
            peerPhone = extras.getString("peerPhone");

            // Generate chat room ID
            if (selfUID.hashCode() < peerUID.hashCode()) {
                chatRoomId = (selfUID + "_" + peerUID);
            } else {
                chatRoomId = (peerUID + "_" + selfUID);
            }

            // Set other user's information
            otherUsername.setText(extras.getString("peerName"));
            Picasso.get().load(extras.getString("peerImage")).into(otherImage);
        }

        // Set onClickListener for back button
        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        // Set onClickListener for back button
        callBtn.setOnClickListener((v) -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                        1);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + peerPhone));
                startActivity(intent);
            }
        });

        // Set onClickListener for send button
        sendImageButton.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUSer(message);
        }));

        // Set up RecyclerView for chat messages
        setupChatRecyclerView();
    }

    // Method to set up RecyclerView for chat messages
    void setupChatRecyclerView(){
        Query query = getChatroomMessageReference(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    // Method to send message to user
    void sendMessageToUSer(String message){

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, currentUserId, Timestamp.now());
        getChatroomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    messageInput.setText(""); // Clear input field after sending message
                }
            }
        });
    }

    // Method to pass user model as intent
    public static  void passUserModelAsIntent(Intent intent, MessageUserModel model){
        intent.putExtra("name", model.getName());
        intent.putExtra("email", model.getEmail());
        intent.putExtra("image", model.getImage());
        intent.putExtra("uid", model.getUid());
    }

    // Method to get chatroom reference
    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // Method to get chatroom message reference
    public static CollectionReference getChatroomMessageReference(String chatRoomId){
        return getChatroomReference(chatRoomId).collection("chats");
    }

    // Method to generate chatroom ID
    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        } else {
            return userId2+"_"+userId1;
        }
    }
}