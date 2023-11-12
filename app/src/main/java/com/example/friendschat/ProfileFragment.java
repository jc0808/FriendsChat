package com.example.friendschat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.friendschat.model.UserModel;
import com.example.friendschat.utils.AndroidUtil;
import com.example.friendschat.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    TextView logoutBtn;

    UserModel currentUserModel;

    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null && data.getData() != null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                        }
                    }
                }
                );
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone_number);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();



        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
        return view;
    }

    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString();

        if(newUsername.isEmpty() || newUsername.length() < 3){
            usernameInput.setError("Username length should be at least 3 characters");
            return;
        }

        currentUserModel.setUsername(newUsername);

        if (selectedImageUri != null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                       updateToFirestone();
                    });
        } else{
            updateToFirestone();
        }

    }

    void updateToFirestone(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        AndroidUtil.showToast(getContext(), "Updated Successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Failed to Update Information");
                    }
                });
    }

    void getUserData(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getContext(), uri,profilePic);
                            }
                        });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhoneNumber());
        });
    }
}