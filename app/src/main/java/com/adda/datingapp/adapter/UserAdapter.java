package com.adda.datingapp.adapter;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adda.datingapp.listeners.UsersListeners;
import com.adda.datingapp.model.User;
import com.adda.datingapp.R;
import com.adda.datingapp.activity.ChatsActivity;
import com.adda.datingapp.databinding.ViewProfileDemoBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    ArrayList<User> list;
    Context context;

    UsersListeners usersListeners;

    public UserAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_profile_demo, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = list.get(position);

        Glide.with(context)
                .load(user.getProfile())
                .placeholder(R.drawable.plasholder)
                .into(holder.binding.viewProfile);

        holder.binding.name.setText(user.getName());




        FirebaseDatabase.getInstance().getReference().child("presence").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    assert status != null;
                    if (!status.isEmpty()) {
                        if (status.equals("offline")) {
                            holder.binding.onlinetxt.setVisibility(View.GONE);
                            holder.binding.bluBtn.setVisibility(View.GONE);
                        } else {
                            holder.binding.onlinetxt.setText(status);
                            holder.binding.onlinetxt.setVisibility(View.VISIBLE);
                            holder.binding.bluBtn.setVisibility(View.VISIBLE);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.binding.itemlist.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatsActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("image", user.getProfile());
            intent.putExtra("uid", user.getUid());
            intent.putExtra("token", user.getToken());
            intent.putExtra("user",user);
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

   public class UserViewHolder extends RecyclerView.ViewHolder {

        ViewProfileDemoBinding binding;
        ImageView videoCall, audioCall;

     UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewProfileDemoBinding.bind(itemView);
         videoCall=itemView.findViewById(R.id.videocallsBtn);
         audioCall=itemView.findViewById(R.id.audiocallBtn);
        }


    }

}
