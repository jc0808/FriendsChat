package com.example.friendschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendschat.adapter.ChatRecyclerAdapter;
import com.example.friendschat.adapter.SearchUserRecyclerAdapter;
import com.example.friendschat.model.ChatMessageModel;
import com.example.friendschat.model.ChatRoomModel;
import com.example.friendschat.model.UserModel;
import com.example.friendschat.utils.AndroidUtil;
import com.example.friendschat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatScreen extends AppCompatActivity {

    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    String chatRoomId;

    ChatRoomModel chatRoomModel;

    ChatRecyclerAdapter adapter;
    ImageView profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.main_back_button);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.chat_message_input);
        profilePic =  findViewById(R.id.profile_pic_image_view);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.setProfilePic(this, uri,profilePic);
                    }
                });

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        otherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener((v) -> {
            String message = messageInput.getText().toString().trim();

            if (message.isEmpty()){
                return;
            }

            sendMessageToUser(message);
        });

        getOrCreateChatRoomModel();

        setUpChatRecyclerView();


    }

    void setUpChatRecyclerView(){
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

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

    void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               chatRoomModel = task.getResult().toObject(ChatRoomModel.class);

               if (chatRoomModel == null){
                   chatRoomModel = new ChatRoomModel(
                           chatRoomId,
                           Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                           Timestamp.now(),
                           ""
                   );

                   FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
               }
           }
        });
    }

    void sendMessageToUser(String message){
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());

        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    messageInput.setText("");
                }
            }
        });
    }
}