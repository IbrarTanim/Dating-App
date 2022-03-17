package com.adda.datingapp.Fragment;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adda.datingapp.R;
import com.adda.datingapp.activity.OrderActivity;
import com.adda.datingapp.adapter.PointAdapter;
import com.adda.datingapp.databinding.FragmentPointsBinding;
import com.adda.datingapp.model.PointModel;
import com.adda.datingapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PointsWalletFragment extends Fragment {

    ArrayList<PointModel> list;
    FirebaseDatabase database;
    FirebaseAuth auth;


    public PointsWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

    }

    FragmentPointsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPointsBinding.inflate(inflater, container, false);


        list = new ArrayList<>();
        PointAdapter pointAdapter = new PointAdapter(list, getContext());
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());
        binding.pointRv.setLayoutManager(LayoutManager);
        binding.pointRv.setAdapter(pointAdapter);

        list.add(new PointModel("10 Taka", "10"));
        list.add(new PointModel("20 Taka", "20"));
        list.add(new PointModel("30 Taka", "30"));
        list.add(new PointModel("40 Taka", "40"));
        list.add(new PointModel("50 Taka", "50"));
        list.add(new PointModel("100 Taka", "120"));
        list.add(new PointModel("300 Taka", "390"));
        list.add(new PointModel("500 Taka", "620"));
        list.add(new PointModel("1000 Taka", "1250"));
        list.add(new PointModel("1500 Taka", "2000"));



        database.getReference()
                .child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()&& snapshot.getValue() !=null){
                            User user=snapshot.getValue(User.class);
                            long coins= (long) user.getCoins();
                            binding.usercoin2.setText("My Points: " + coins);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("presence").child(FirebaseAuth.getInstance().getUid()).setValue("Online");
    }
}