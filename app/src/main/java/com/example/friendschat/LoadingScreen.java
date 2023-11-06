package com.example.friendschat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.friendschat.utils.FirebaseUtil;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (FirebaseUtil.isLoggedIn()){
                    startActivity(new Intent(LoadingScreen.this, MainActivity.class));
                } else {
                    startActivity(new Intent(LoadingScreen.this, LoginScreen.class));
                }

                finish();
            }
        }, 1000);
    }
}