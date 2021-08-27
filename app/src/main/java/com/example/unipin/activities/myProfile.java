package com.example.unipin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unipin.R;
import com.example.unipin.model.ProfileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class myProfile extends AppCompatActivity {
        private ImageView profilePicture;
        private TextView profileName,profileLastName,profileEmail,profilePhone;
        private  FirebaseAuth mAuth;
        private DatabaseReference databaseReference;
        private String currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        init();

        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    Toast.makeText(myProfile.this, snapshot.getKey(), Toast.LENGTH_SHORT).show();
                    ProfileModel profileModel=snapshot.getValue(ProfileModel.class);
                    if(currentUser.equals(profileModel.getEmail())) {
                        Toast.makeText(myProfile.this, profileModel.getEmail(), Toast.LENGTH_SHORT).show();
                        profileEmail.setText("Email: " + profileModel.getEmail());
                        profileName.setText("First Name: " + profileModel.getFirstName());
                        profileLastName.setText("Last Name: " + profileModel.getLastName());
                        profilePhone.setText("Phone number: " + profileModel.getPhoneNumber());
                        Glide.with(myProfile.this).load(profileModel.getImage()).into(profilePicture);
                    }



                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,Universities.class);
        startActivity(i);
        finish();
    }

    public void init(){
        profilePicture=findViewById(R.id.profile_pic_detail);
        profileName=findViewById(R.id.profile_first_name);
        profileLastName=findViewById(R.id.profile_last_name);
        profileEmail=findViewById(R.id.profile_email_tv);
        profilePhone=findViewById(R.id.profile_phoneNumber_tv);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser().getEmail();

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

}