package com.example.unipin.adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unipin.R;
import com.example.unipin.Share;
import com.example.unipin.activities.Notification;
import com.example.unipin.activities.NotificationDetail;
import com.example.unipin.activities.Universities;
import com.example.unipin.model.NotificationModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotificAdapter extends RecyclerView.Adapter<NotificAdapter.ViewHolder> {
    ArrayList<NotificationModel> notificationList = new ArrayList<>();
    Context context;
    DatabaseReference reference;
    String uniName,deptName;
   FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    String email;



    public NotificAdapter(ArrayList<NotificationModel> notificationList, Context context,String uniName,String deptName) {
        this.notificationList = notificationList;
        this.context = context;
        this.uniName=uniName;
        this.deptName=deptName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.notification_layout,parent,false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item= notificationList.get(position);

        Glide.with(context).load(item.getImage()).into(holder.mNotification);

        String img = item.getImage();

        holder.mDate.setText("Date: "+item.getDate());
        holder.description.setText(item.getText());



        if(firebaseUser.getEmail().equalsIgnoreCase("shahid@gmail.com"))
        {
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.app.AlertDialog.Builder ad =new AlertDialog.Builder(context);
                    ad.setTitle("App will close");
                    ad.setMessage("Are u sure you want to exit?");
                    ad.setCancelable(false);
                    ad.setIcon(R.drawable.ic_baseline_warning_24);
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseStorage.getInstance().getReference().
                                    child("images").child(item.getImage()).delete();
                            reference= FirebaseDatabase.getInstance()
                                    .getReference().child(uniName).child(deptName);
                            reference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if(snapshot.exists()){
                                        NotificationModel model = snapshot.getValue(NotificationModel.class);

                                        if(model.getImage().equalsIgnoreCase(img)){
                                            String s = snapshot.getKey().toString();
                                            FirebaseDatabase.getInstance().getReference().child(uniName).child(deptName)
                                                    .child(s).setValue(null);

                                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context, Notification.class);
                                            intent.putExtra("uniname", uniName);
                                            intent.putExtra("deptname", deptName);
                                            context.startActivity(intent);
                                            ((Activity)context).finish();
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
                    });
                    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    ad.show();


                }
            });

        }
        else
        {
            holder.mDelete.setVisibility(View.INVISIBLE);
        }


        holder.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Share share = new Share((Activity) context,context,item.getImage(),R.layout.loader_view,R.id.custom_loader,R.drawable.loading);

            }
        });
        holder.mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImageNew(System.currentTimeMillis()+"",item.getImage());





            }
        });
        holder.seeNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, NotificationDetail.class);
                i.putExtra("image",item.getImage());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mNotification, mDownload, mShare,mDelete,seeNoti;
        TextView mDate, description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNotification=itemView.findViewById(R.id.notification_image);
            seeNoti=itemView.findViewById(R.id.eye_click);
            mDownload=itemView.findViewById(R.id.download_click);
            mShare=itemView.findViewById(R.id.share_click);
            mDate = itemView.findViewById(R.id.notification_date);
            description = itemView.findViewById(R.id.notification);
            mDelete = itemView.findViewById(R.id.delete_click);
        }

    }
    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,File.separator + filename + ".jpg");
            ProgressDialog progressDialog=new ProgressDialog(context);
            progressDialog.setTitle("Downloading");
            progressDialog.show();
            dm.enqueue(request);
            progressDialog.dismiss();
            Toast.makeText(context, "downloading completed", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(context, "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
