package com.example.unipin.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unipin.R;
import com.example.unipin.activities.Departments;
import com.example.unipin.activities.Notification;
import com.example.unipin.model.DeptModel;
import com.example.unipin.model.UniversityModel;

import java.util.ArrayList;

public class DeptAdapter extends RecyclerView.Adapter<DeptAdapter.ViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    Context context;
    String uniName;

    public DeptAdapter(ArrayList<String> list, Context context, String uniName) {
        this.list = list;
        this.context = context;
        this.uniName = uniName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.department_layout,parent,false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // String item= list.get(position);
 //       Toast.makeText(context, item, Toast.LENGTH_SHORT).show();

  //      Toast.makeText(context, item.length(), Toast.LENGTH_SHORT).show();
        holder.mDeptName.setText(list.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, list.get(position)+" CLICKED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Notification.class);
                intent.putExtra("uniname", uniName);
                intent.putExtra("deptname", list.get(position));
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout mView;
        TextView mDeptName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView.findViewById(R.id.department_layout);
            mDeptName=itemView.findViewById(R.id.department_tv);

        }
    }
}
