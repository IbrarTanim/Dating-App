/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

package com.adda.datingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adda.datingapp.model.User;
import com.adda.datingapp.R;
import com.adda.datingapp.activity.ChatsActivity;
import com.adda.datingapp.databinding.CircelProfileDemoBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CircleProfileAdapter extends RecyclerView.Adapter<CircleProfileAdapter.CircleProfileViewHolder> {

    Context context;
    ArrayList<User> list;

    public CircleProfileAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CircleProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.circel_profile_demo, parent, false);

        return new CircleProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  CircleProfileAdapter.CircleProfileViewHolder holder, int position) {
        User user= list.get(position);
        Glide.with(context)
                .load(user.getProfile())
                .placeholder(R.drawable.plasholder)
                .into(holder.binding.circleProfile);
        holder.binding.cirName.setText(user.getName());

        FirebaseDatabase.getInstance().getReference().child("presence").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status=snapshot.getValue(String.class);
                    if (!status.isEmpty()){
                        if (status.equals("offline")){
                            holder.binding.bluBtn2.setVisibility(View.GONE);
                        }else {
                            holder.binding.bluBtn2.setVisibility(View.VISIBLE);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.circleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("image", user.getProfile());
                intent.putExtra("uid", user.getUid());
                intent.putExtra("token", user.getToken());
                intent.putExtra("user",user);
                context.startActivity(intent);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CircleProfileViewHolder extends RecyclerView.ViewHolder{

        CircelProfileDemoBinding binding;
        public CircleProfileViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding=CircelProfileDemoBinding.bind(itemView);
        }
    }
}
