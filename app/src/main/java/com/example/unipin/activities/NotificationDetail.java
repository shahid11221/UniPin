package com.example.unipin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.unipin.R;

public class NotificationDetail extends AppCompatActivity {
    private ImageView notiImg;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        notiImg=findViewById(R.id.noti_image);
        bundle = getIntent().getExtras();
        String img=bundle.getString("image");
        Glide.with(this).load(img).into(notiImg);


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}