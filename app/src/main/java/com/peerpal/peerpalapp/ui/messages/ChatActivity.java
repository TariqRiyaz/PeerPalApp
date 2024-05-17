package com.peerpal.peerpalapp.ui.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.peerpal.peerpalapp.R;
import com.peerpal.peerpalapp.ui.peers.PeersClass;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    // Declare variables

    PeersClass otherUser;
    String chatRoomId;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendImageButton;
    ImageButton backBtn;
    TextView otherUsername;
    ImageView otherImage;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the layout
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Add setter methods here
        //otherUser =

        // Initialize views
        messageInput = findViewById(R.id.chat_message_input);
        sendImageButton = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
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
                    sendNotification(message);

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

     void sendNotification(String message)
     {
         // Current username, message, current id and other user token

         FirebaseFirestore.getInstance().collection("peers").document(FirebaseAuth.getInstance().getUid())
                 .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        PeersClass currentUser = task.getResult().toObject(PeersClass.class);

                        try{
                            JSONObject jsonObject = new JSONObject();

                            JSONObject notificationObj = new JSONObject();
                            notificationObj.put("title", currentUser.getPeersName());
                            notificationObj.put("body", message);

                            JSONObject dataObj = new JSONObject();
                            dataObj.put("uid", currentUser.getPeersUID());

                            jsonObject.put("notification", notificationObj);
                            jsonObject.put("data", dataObj);
                            jsonObject.put("to", otherUser.getFcmToken());

                            callApi(jsonObject);

                        }catch (Exception error)
                        {


                        }


                    }


                 });

     }

     void callApi(JSONObject jsonObject)
     {
         MediaType JSON = MediaType.get("application/json");
         OkHttpClient client = new OkHttpClient();

         String url = "https://fcm.googleapis.com/fcm/send";
         RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
         Request request = new Request.Builder()
                 .url(url)
                 .post(body)
                 .header("Authorization", "Bearer AAAATnBGAJM:APA91bEwn2surGxt6t3wp_KiU-19R0S7l2HrI30DPwTLmzNy0H7NBtoIOZQibh-0fkueBFeuV7kmdJ74KsdFiV4MeVEnuvpv5t4-8Jp-tv0jWBK1MbZkL6fET3TUmrkkE_LqMM-lm07e")
                 .build();
         client.newCall(request).enqueue(new Callback() {
             @Override
             public void onFailure(@NonNull Call call, @NonNull IOException e) {

             }

             @Override
             public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

             }
         });

     }

}