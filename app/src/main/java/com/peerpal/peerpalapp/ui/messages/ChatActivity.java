package com.peerpal.peerpalapp.ui.messages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    String chatRoomId;
    ChatRoomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendImageButton;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();


        chatRoomId = "IvmNNuEZKIUkaxPf64Qga26aq9S2_M47rmpT9MvR13MneP7DMUi2fViD2";
        messageInput = findViewById(R.id.chat_message_input);
        sendImageButton = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        sendImageButton.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUSer(message);
        }));

        setupChatRecyclerView();

    }

    void setupChatRecyclerView(){
        Log.d("OtherUserId", chatRoomId);
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

    void sendMessageToUSer(String message){

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, currentUserId, Timestamp.now());
        getChatroomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    messageInput.setText("");
                }
            }
        });
    }

//    void getChatRoomModel(){
//        getChatroomReference(chatRoomId).get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                chatroomModel = task.getResult().toObject(ChatRoomModel.class);
//                if(chatroomModel==null){
//                    chatroomModel = new ChatRoomModel(
//                            chatRoomId,
//                            Arrays.asList(currentUserId, "IvmNNuEZKIUkaxPf64Qga26aq9S2"),
//                            Timestamp.now(),
//                            ""
//                    );
//                    getChatroomReference(chatRoomId).set(chatroomModel);
//                }
//            }
//        });
//    }

    public static  void passUserModelAsIntent(Intent intent, MessageUserModel model){
        intent.putExtra("name", model.getName());
        intent.putExtra("email", model.getEmail());
        intent.putExtra("image", model.getImage());
        intent.putExtra("uid", model.getUid());

    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatRoomId){
        return getChatroomReference(chatRoomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        } else {
            return userId2+"_"+userId1;
        }
    }
}