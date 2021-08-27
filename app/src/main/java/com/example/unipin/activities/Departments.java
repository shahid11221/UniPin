package com.example.unipin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unipin.R;
import com.example.unipin.adapters.DeptAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;

public class Departments extends AppCompatActivity {

    Bundle mGetValues;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    String uniName;

    RecyclerView mDeptList;
    ArrayList<String> mDept;
    DeptAdapter mDeptAdapter;
    LinearLayoutManager mLinearLayoutManager;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceDept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departments);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        init();
        loadFireBaseData();
    }

    public void init(){
        mGetValues = getIntent().getExtras();
        mDeptList = findViewById(R.id.my_dept_view);
        mDept = new ArrayList<String>();
        mLinearLayoutManager= new LinearLayoutManager(this);
        mDeptList.setLayoutManager(mLinearLayoutManager);
        progressDialog = new ProgressDialog(Departments.this);
        progressDialog.setMessage("Data loading");
        progressDialog.show();

        uniName = mGetValues.getString("uniname");

    }

    private void loadFireBaseData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().
                child(mGetValues.getString("uniname")).child(mGetValues.getString("uniname"));
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    progressDialog.dismiss();
                    String dept = dataSnapshot.getValue().toString();
                    mDept.add(dept);
                    mDeptAdapter = new DeptAdapter(mDept,Departments.this,uniName);
                    mDeptAdapter.notifyDataSetChanged();
                    mDeptList.setAdapter(mDeptAdapter);

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
            menuInflater.inflate(R.menu.add_department,menu);
            return true;
        }else {
            return false;
        }

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_dept:

                databaseReferenceDept = FirebaseDatabase.getInstance().getReference().child(mGetValues.getString("uniname"));

                final DialogPlus dialogPlus=DialogPlus.newDialog(Departments.this)
                        .setContentHolder(new ViewHolder(R.layout.add_dept))
                        .setExpanded(true,700)
                        .create();

                View myview=dialogPlus.getHolderView();
                EditText mDeptEt=myview.findViewById(R.id.branch_name_et);
                Button mAddDeptBtn=myview.findViewById(R.id.add_branch);

                dialogPlus.show();

                mAddDeptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dept = mDeptEt.getText().toString().trim();

                        if(!dept.equals("")){
                            databaseReferenceDept.child(mGetValues.getString("uniname")).push().setValue(dept).
                                    addOnCompleteListener(Departments.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            dialogPlus.dismiss();
                                            Toast.makeText(Departments.this, "Dept Successfully Added", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Departments.this, Departments.class);
                                            intent.putExtra("uniname", mGetValues.getString("uniname"));
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        } else {
                            mDeptEt.setError("Enter Department Name");
                        }

                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Departments.this, Universities.class);
        startActivity(intent);
        finish();
    }
}