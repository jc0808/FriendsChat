package com.example.friendschat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginScreen extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneNumber;
    Button sendCode;
//    ProgressBar progressBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneNumber = findViewById(R.id.login_phone_number);
        sendCode = findViewById(R.id.send_code_btn);
//        progressBar = findViewById(R.id.progree)
        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        sendCode.setOnClickListener((s) -> {
//           if (! countryCodePicker.isValidFullNumber()){
//               phoneNumber.setError("Phone number is not valid");
//               return;
//           }

            Intent intent = new Intent(LoginScreen.this, LoginScreen2.class);

           intent.putExtra("phoneNumber", countryCodePicker.getFullNumberWithPlus());
           startActivity(intent);
        });
    }
}