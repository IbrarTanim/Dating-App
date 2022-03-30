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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adda.datingapp.model.User;
import com.adda.datingapp.R;
import com.adda.datingapp.activity.ChatsActivity;
import com.adda.datingapp.databinding.ConversionDemoBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder> {

    ArrayList<User> list;
    Context context;


    public ConversionAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conversion_demo, parent, false);
        return new ConversionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionAdapter.ConversionViewHolder holder, int position) {
        int itemPos = position;
        User user = list.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long lastMsgTime = snapshot.child("lastMsgTime").getValue(long.class);
                            if (snapshot.child("lstMsgSeen").getValue(String.class) != null){
                                String lstMsgSeen = snapshot.child("lstMsgSeen").getValue(String.class);
                                if (lstMsgSeen.equals("no")){
                                    holder.binding.lastMsg.setTextColor(ContextCompat.getColor(context, R.color.black));
                                }else {
                                    holder.binding.lastMsg.setTextColor(ContextCompat.getColor(context, R.color.gray));
                                }
                            }else {
                                holder.binding.lastMsg.setTextColor(ContextCompat.getColor(context, R.color.gray));
                            }

                            holder.binding.lastMsg.setText(lastMsg);

                            Glide.with(context)
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.plasholder)
                                    .into(holder.binding.MProfile);
                            holder.binding.MName.setText(user.getName());

                            holder.binding.item.setOnClickListener(new View.OnClickListener() {
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
                            holder.binding.mainLayout.setVisibility(View.VISIBLE);
                        }else {
                            holder.binding.mainLayout.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ConversionViewHolder extends RecyclerView.ViewHolder {

        ConversionDemoBinding binding;

        public ConversionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ConversionDemoBinding.bind(itemView);
        }
    }
}
