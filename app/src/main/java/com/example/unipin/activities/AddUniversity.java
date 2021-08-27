package com.example.unipin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.unipin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddUniversity extends AppCompatActivity {
    private Button addUniBtn;
    private ImageView uniImageIv;
    private EditText uniNameEt;
    String uniName="",
            uniImagePath="";
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    String email;


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    uniImageIv.setImageURI(selectedImage);
                    progressDialog.show();
                    mStorageRef.child("images/").child(email+".jpg").putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            uniImagePath = uri.toString();
                                            progressDialog.cancel();
                                            Toast.makeText(AddUniversity.this, "Path selected", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    uniImagePath="no_image";
                                    progressDialog.cancel();
                                    Toast.makeText(AddUniversity.this, "Path not selected", Toast.LENGTH_SHORT).show();
                                }
                            }

                            Toast.makeText(AddUniversity.this, "image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_university);
        init();

    }
    public  void init()    {   FirebaseAuth mAuth;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        email=mAuth.getCurrentUser().getEmail();
        addUniBtn=findViewById(R.id.add_uni_btn);
        uniImageIv=findViewById(R.id.image_uni);
        uniNameEt=findViewById(R.id.name_uni_et);
        progressDialog=new ProgressDialog(AddUniversity.this);
        progressDialog.setMessage("Uploading Image");
        progressDialog.setIcon(R.drawable.ic_baseline_android_24);


    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(AddUniversity.this, Universities.class);
        startActivity(i);
        finish();
    }

    public void AddUniversityMethod(View view) {
        uniName = uniNameEt.getText().toString();
        if(uniName.equals(""))
        {
            uniNameEt.setError("Enter University name");
        }
        else
        {
            if(!uniImagePath.equals("")) {
                HashMap<String, String> myData = new HashMap<String, String>();
                myData.put("universityName", uniName);
                myData.put("universityImage", uniImagePath);


                databaseReference.child("universities").child(uniName).setValue(myData).addOnCompleteListener(AddUniversity.
                        this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddUniversity.this, "University Added", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddUniversity.this, Universities.class));
                            finish();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "Select jpg file", Toast.LENGTH_SHORT).show();
            }

        }




    }

    public void AddImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0);
    }
}