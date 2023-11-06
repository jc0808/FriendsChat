package com.example.friendschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.friendschat.model.UserModel;
import com.example.friendschat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserInfoScreen extends AppCompatActivity {

    EditText usernameInput;
    Button finishB;

    String phoneNumber;

    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_screen);

        usernameInput = findViewById(R.id.user_name);
        finishB = findViewById(R.id.user_info_next_btn);

        phoneNumber = getIntent().getExtras().getString("phoneNumber");

        getUsername();

        finishB.setOnClickListener((v -> {
            setUsername();
        }));
    }

    void setUsername(){

            String username = usernameInput.getText().toString();

            if(username.isEmpty() || username.length() < 3){
                usernameInput.setError("Username length should be at least 3 characters");
                return;
            }

            if (userModel != null){
                userModel.setUsername(username);
            } else {
                userModel = new UserModel(username, phoneNumber, Timestamp.now(), FirebaseUtil.currentUserId());
            }

            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(UserInfoScreen.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });


    }

    void getUsername(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);

                    if (userModel != null){
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }
}