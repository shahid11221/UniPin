package com.example.unipin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unipin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    private EditText emailLoginEt,passwordLoginEt;
    private String email;
    boolean netConnection;
    private FirebaseAuth mAuth;
    CheckBox rememberMeCb;
    String userN,passW;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        rememberMeCb=findViewById(R.id.remember_me_cb);
        emailLoginEt=findViewById(R.id.email_login_et);
        passwordLoginEt=findViewById(R.id.password_login_et);
        Button loginBtn = findViewById(R.id.login_btn);
        TextView gotoSignupTw = findViewById(R.id.goto_signup_tw);
        sharedPreferences =getSharedPreferences("AdminCredentialsSaver",MODE_PRIVATE);

        gotoSignupTw.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent i=new Intent(LoginScreen.this, SignupScreen.class);
             startActivity(i);
              finish();

          }
      });
        mAuth = FirebaseAuth.getInstance();

        CheckInternetConnection();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });

        userN=sharedPreferences.getString("username","");
        passW=sharedPreferences.getString("password","");
        emailLoginEt.setText(userN+"");
        passwordLoginEt.setText(passW+"");
        rememberMeCb.setChecked(true);

    }
    private void UserLogin() {
        email=emailLoginEt.getText().toString().trim();
        String password = passwordLoginEt.getText().toString().trim();

        if(email.isEmpty()){
            emailLoginEt.setError("Email Required");
        }else if(password.isEmpty()){
            passwordLoginEt.setError("password Required");
        }else if (password.length() < 8) {
            passwordLoginEt.setError("At least 6 character password");
        }else if ((!Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            emailLoginEt.setError("Wrong format");
        }else if(netConnection){


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        final SharedPreferences.Editor editor=sharedPreferences.edit();

                        if(email.equals("yaseen@gmail.com"))
                        {    progressDialog = new ProgressDialog(LoginScreen.this);
                            progressDialog.setMessage("Loggin in");
                            progressDialog.show();

                            if(rememberMeCb.isChecked())
                            {
                                editor.putString("username",emailLoginEt.getText().toString());
                                editor.putString("password",passwordLoginEt.getText().toString());
                                editor.apply();
                            }
                            else
                            {
                                emailLoginEt.setText(null);
                                passwordLoginEt.setText(null);
                                editor.putString("username",emailLoginEt.getText().toString());
                                editor.putString("password",passwordLoginEt.getText().toString());
                                editor.commit();

                            }
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginScreen.this, Universities.class));
                            finish();
                        }
                        else
                        {
                            progressDialog = new ProgressDialog(LoginScreen.this);
                            progressDialog.setMessage("Loggin in");
                            progressDialog.show();
                            if(rememberMeCb.isChecked())
                            {
                                editor.putString("username",emailLoginEt.getText().toString());
                                editor.putString("password",passwordLoginEt.getText().toString());
                                editor.apply();
                            }
                            else
                            {
                                emailLoginEt.setText(null);
                                passwordLoginEt.setText(null);
                                editor.putString("username",emailLoginEt.getText().toString());
                                editor.putString("password",passwordLoginEt.getText().toString());
                                editor.commit();

                            }
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginScreen.this, Universities.class));
                            finish();
                        }
                    }

                    else{
                        String error = task.getException().toString();
                        Toast.makeText(LoginScreen.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else {
            Toast.makeText(this, "No Internet found", Toast.LENGTH_SHORT).show();
        }
    }
    public void CheckInternetConnection(){
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad =new AlertDialog.Builder(LoginScreen.this);
        ad.setTitle("App will close");
        ad.setMessage("Are u sure you want to exit?");
        ad.setCancelable(false);
        ad.setIcon(R.drawable.ic_baseline_warning_24);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finishAndRemoveTask();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        ad.show();
    }
}