package com.example.friendschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendschat.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginScreen2 extends AppCompatActivity {

    String phoneNumber;

    EditText codeInput;
    TextView resendCode;

    Button login;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    Long timeOutSeconds = 60L;

    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen2);

        codeInput = findViewById(R.id.login_code);
        resendCode = findViewById(R.id.login_resend_code);
        login = findViewById(R.id.login_next_btn);





        phoneNumber = getIntent().getExtras().getString("phoneNumber");
//        Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_LONG).show();

        sendCode(phoneNumber, false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeInput.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);
                singIn(credential);
//                setInProgress(true);
            }
        });

        resendCode.setOnClickListener((v) -> {
            sendCode(phoneNumber, true);
        });



    }

    void sendCode(String phoneNumber, boolean isResend){
//        setInProgress(true);

        startResendTimer();

        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        singIn(phoneAuthCredential);
//                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(), "Sign in verification failed");
//                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(), "Code Sent Successfully!");
                    }
                });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void singIn(PhoneAuthCredential phoneAuthCredential){
//        setInProgress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
//                setInProgress(false);
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginScreen2.this, UserInfoScreen.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }else {
                    AndroidUtil.showToast(getApplicationContext(), "Sign In  Verification failed");
                }
            }
        });
    }


    void startResendTimer(){
        resendCode.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeOutSeconds--;
                resendCode.setText("Resend Code in " + timeOutSeconds + " seconds");

                if (timeOutSeconds <= 0){
                    timeOutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendCode.setEnabled(true);
                        }
                    });
                }
            }
        },0, 1000);
    }

//    void setInProgress(boolean inProgress){
//        if (inProgress){
//            progressBar.setVisibility(View.VISIBLE);
//            login.setVisibility(View.GONE);
//        } else {
//            progressBar.setVisibility(View.Gone);
//            login.setVisibility(View.VISIBLE);
//        }
//    }
}