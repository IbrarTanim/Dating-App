package com.adda.datingapp.Fragment;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adda.datingapp.adapter.CircleProfileAdapter;
import com.adda.datingapp.adapter.ConversionAdapter;
import com.adda.datingapp.model.User;
import com.adda.datingapp.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChatsFragment extends Fragment {

    ArrayList<User> list;
    ArrayList<User> circleList;
    FirebaseDatabase database;
    FirebaseAuth auth;

    private List<String> chatRoomLink;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);


        circleList = new ArrayList<>();
        chatRoomLink = new ArrayList<>();
        CircleProfileAdapter circleProfileAdapter = new CircleProfileAdapter(getContext(), circleList);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LayoutManager.setReverseLayout(true);
        LayoutManager.setStackFromEnd(true);
        binding.cicleProfileRV.setLayoutManager(LayoutManager);
        binding.cicleProfileRV.setAdapter(circleProfileAdapter);

        database.getReference()
                .child("Partner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                circleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User users = dataSnapshot.getValue(User.class);
                    if (!Objects.equals(users.getUid(), FirebaseAuth.getInstance().getUid()))
                        circleList.add(users);
                }
                circleProfileAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        list = new ArrayList<>();

        ConversionAdapter adapter = new ConversionAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.massageRV.setLayoutManager(linearLayoutManager);
        binding.massageRV.setAdapter(adapter);

        database.getReference()
                .child("Partner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                 User  user = dataSnapshot.getValue(User.class);
                    if (!Objects.equals(user.getUid(), FirebaseAuth.getInstance().getUid()))
                        list.add(user);
                }
                /*if (list != null && !list.isEmpty()){
                    for (User user : list){

                    }
                }*/
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



        return  binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        database.getReference().child("presence").child(auth.getUid()).setValue("online");
    }

    @Override
    public void onPause() {
        database.getReference().child("presence").child(auth.getUid()).setValue("offline");
        super.onPause();
    }




}