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
import com.example.unipin.model.UniversityModel;

import java.util.ArrayList;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.ViewHolder> {
    ArrayList<UniversityModel> universityModelArrayList= new ArrayList<>();
    Context context;

    public UniversityAdapter(ArrayList<UniversityModel> universityModelArrayList, Context context) {
        this.universityModelArrayList = universityModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.university_layout,parent,false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UniversityModel universityModelItem=universityModelArrayList.get(position);


        holder.nameRecyclerView.setText(universityModelItem.getUniversityName());
        Glide.with(context).load(universityModelItem.getUniversityImage()).into(holder.imageRecyclerView);

        holder.layoutRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Departments.class);
                intent.putExtra("uniname",universityModelItem.getUniversityName());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return universityModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layoutRecyclerView;
        ImageView imageRecyclerView;
        TextView nameRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRecyclerView=itemView.findViewById(R.id.layout_recycler_view);
            imageRecyclerView=itemView.findViewById(R.id.image_recycler_view);
            nameRecyclerView=itemView.findViewById(R.id.tw_recycler_view);
        }
    }
}
