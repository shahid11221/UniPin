package com.example.unipin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unipin.R;
import com.example.unipin.adapters.DeptAdapter;
import com.example.unipin.adapters.NotificAdapter;
import com.example.unipin.model.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Notification extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    String uniName,deptName;
    Bundle mGetValues;
    ImageView mImage;
    DatabaseReference databaseReferenceNoti;
    StorageReference mStorageRef;
    ProgressDialog progressDialog;
    String notiImagePath="";
    private EditText notiDescription;
    RecyclerView mNotiList;
    ArrayList<NotificationModel> mNoti;
    NotificAdapter mNotiAdapter;
    LinearLayoutManager mLinearLayoutManager;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceDept;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    mImage.setImageURI(selectedImage);

                    mStorageRef.child("images/").child(System.currentTimeMillis()+".jpg").putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            notiImagePath = uri.toString();

                                            Toast.makeText(Notification.this, "Path selected", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    notiImagePath = "no_image";

                                    Toast.makeText(Notification.this, "Path not selected", Toast.LENGTH_SHORT).show();
                                }
                            }

                            Toast.makeText(Notification.this, "image uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuth = FirebaseAuth.getInstance();
        user =mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        init();
        loadFireBaseData();
    }

    public void init(){
        mGetValues = getIntent().getExtras();
        uniName = mGetValues.getString("uniname");
        deptName = mGetValues.getString("deptname");
        notiDescription = findViewById(R.id.notiText);
        mNotiList = findViewById(R.id.my_Noti_view);
        mNoti = new ArrayList<NotificationModel>();
        mLinearLayoutManager= new LinearLayoutManager(this);
        mNotiList.setLayoutManager(mLinearLayoutManager);
        progressDialog = new ProgressDialog(Notification.this);
        progressDialog.setMessage("Data loading");
        progressDialog.show();
    }

    private void loadFireBaseData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().
                child(mGetValues.getString("uniname")).child(deptName);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    progressDialog.dismiss();
                    NotificationModel noti = dataSnapshot.getValue(NotificationModel.class);
                    mNoti.add(noti);
                    mNotiAdapter = new NotificAdapter(mNoti,Notification.this,uniName,deptName);
                    mNotiAdapter.notifyDataSetChanged();
                    mNotiList.setAdapter(mNotiAdapter);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(user.getEmail().equalsIgnoreCase("yaseen@gmail.com")){
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.add_notification,menu);
            return true;
        }else {
            return false;
        }

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_notofication:

                databaseReferenceNoti = FirebaseDatabase.getInstance().getReference().child(uniName).child(deptName);

                final DialogPlus dialogPlus=DialogPlus.newDialog(Notification.this)
                        .setContentHolder(new ViewHolder(R.layout.add_noti))
                        .setExpanded(true,1000)
                        .create();

                View myview=dialogPlus.getHolderView();

                mImage= myview.findViewById(R.id.notification_image);
                Button mAddNotiBtn = myview.findViewById(R.id.add_noti);
                EditText notiText = myview.findViewById(R.id.notiText);

                dialogPlus.show();

                mAddNotiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String text = notiText.getText().toString().trim();
                    /*    if(notiImagePath.equals(null))
                        {
                            Toast.makeText(Notification.this, "Select image", Toast.LENGTH_SHORT).show();
                        }*/
                        if(notiText.getText().toString().equals(""))
                        {
                            notiText.setError("Enter notification description");
                        }
                        else if(!notiImagePath.equals("")){

                            HashMap<String, String> myData = new HashMap<String, String>();
                            myData.put("image", notiImagePath);
                            myData.put("date", date);
                            myData.put("text",text);
                            databaseReferenceNoti.push().setValue(myData).
                                    addOnCompleteListener(Notification.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            dialogPlus.dismiss();
                                            Intent intent = new Intent(Notification.this, Notification.class);
                                            intent.putExtra("uniname", mGetValues.getString("uniname"));
                                            intent.putExtra("deptname", mGetValues.getString("deptname"));
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(Notification.this, "Notification Successfully Added", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                        else {
                            Toast.makeText(Notification.this, "Select Image", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,0);
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Departments.class);
        intent.putExtra("uniname", uniName);
        intent.putExtra("deptname", deptName);
        startActivity(intent);
        finish();
    }
}