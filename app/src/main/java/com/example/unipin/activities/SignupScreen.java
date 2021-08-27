package com.example.unipin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unipin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignupScreen extends AppCompatActivity {
    private EditText emailSignupEt,passwordSignupEt,confirmPasswordEt,firstName,lastName,phoneNumber;
    private Button signupBtn;
    private TextView gotoLoginTw;

    private FirebaseAuth mAuth;
    boolean netConnection;
    private DatabaseReference databaseReference;
    private String email,pass,c_pass,fName,lName,pNumber;
    private String profilePicturePath;
    private ImageView profilePicture;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        init();
        InternetConnection();
        UserSignup();
        gotoLoginTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoLogin = new Intent(SignupScreen.this,LoginScreen.class);
                startActivity(gotoLogin);
                finish();
            }
        });
    }
    private void UserSignup() {

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emailSignupEt.getText().toString().trim();
                pass=passwordSignupEt.getText().toString().trim();
                c_pass=confirmPasswordEt.getText().toString().trim();
                fName=firstName.getText().toString().trim();
                lName=lastName.getText().toString().trim();
                pNumber=phoneNumber.getText().toString().trim();


                if(netConnection)
                {
                    if (email.equals("")) {
                        emailSignupEt.setError("Email required!");
                    }
                    else if (profilePicturePath.equals(null)) {
                        Toast.makeText(SignupScreen.this, "Select a Profile Picture", Toast.LENGTH_SHORT).show();;
                    }
                    else if (pass.equals("")) {
                        passwordSignupEt.setError("Password Required!");
                    } else if (pass.length() <8) {
                        passwordSignupEt.setError("At least 8 character password");
                    } else if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                        emailSignupEt.setError("Wrong format");
                    }  else if (!(pass.equals(c_pass))) {
                        confirmPasswordEt.setError("password not match");
                    }


                    else {
                        // todo new user registration
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(SignupScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String currentUser = mAuth.getCurrentUser().getUid();

                                    HashMap<String,String> myData = new HashMap<String,String>();
                                    myData.put("id",currentUser);
                                    myData.put("email",email);
                                    myData.put("password",pass);
                                    myData.put("firstName",fName);
                                    myData.put("lastName",lName);
                                    myData.put("PhoneNumber",pNumber);
                                    myData.put("image",profilePicturePath);





                                    databaseReference.child("users").child(currentUser).setValue(myData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            {
                                                Toast.makeText(SignupScreen.this, "User saved", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    startActivity(new Intent(SignupScreen.this, LoginScreen.class));
                                    Toast.makeText(SignupScreen.this, "Welcome"/*+ currentUser*/, Toast.LENGTH_SHORT).show();
                                    finish();

                                }else{
                                    String error = task.getException().toString();
                                    Toast.makeText(SignupScreen.this, error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(SignupScreen.this, "No Internet Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void InternetConnection() {
        netConnection=false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            netConnection = true;
        }
        else{
            netConnection = false;
        }
    }
    private void init(){ profilePicture =findViewById(R.id.cnic_img);
        emailSignupEt=findViewById(R.id.email_signup_et);
        passwordSignupEt=findViewById(R.id.password_signup_et);
        confirmPasswordEt=findViewById(R.id.confirm_password_signup_et);
        signupBtn=findViewById(R.id.signup_btn);
        firstName = findViewById(R.id.first_name_et);
        lastName = findViewById(R.id.last_name_et);
        phoneNumber = findViewById(R.id.phone_number_et);
        gotoLoginTw=findViewById(R.id.goto_login);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    profilePicture.setImageURI(selectedImage);

                    mStorageRef.child("images/").child(System.currentTimeMillis()+".jpg").
                            putFile(selectedImage).
                            addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    if (taskSnapshot.getMetadata() != null) {
                                        if (taskSnapshot.getMetadata().getReference() != null) {
                                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    profilePicturePath = uri.toString();
                                                    Toast.makeText(SignupScreen.this, "Path selected", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            profilePicturePath ="no_image";
                                            Toast.makeText(SignupScreen.this, "Path not selected", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    Toast.makeText(SignupScreen.this, "image uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                break;



        }
    }

    public void selectCnicImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,LoginScreen.class);
        startActivity(i);
        finish();
    }
}