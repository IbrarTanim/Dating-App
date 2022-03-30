/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 *  if your need any help knock this number +8801776254584 whatsapp
 */

package com.adda.datingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.adda.datingapp.databinding.ActivityOrderBinding;
import com.adda.datingapp.model.PointModel;
import com.adda.datingapp.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class OrderActivity extends AppCompatActivity {

    ActivityOrderBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String Name;
    String Email;
    double UserPoint;
    User user;
    double Point;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        PointModel pointModel = (PointModel) getIntent().getSerializableExtra("object");

        binding.takapoint.setText(pointModel.getPoint() + " Point = " + pointModel.getTaka());


        database.getReference()
                .child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(User.class);
                            Name = user.getName();
                            Email = user.getEmail();
                            UserPoint = user.getCoins();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AccountNum=binding.accountNum.getText().toString();
                String Taka = pointModel.getTaka();
                Point = Double.parseDouble(pointModel.getPoint());
                String num=binding.accountNum.getText().toString().trim();

                HashMap<String, Object> order = new HashMap<>();
                order.put("name", Name);
                order.put("email", Email);
                order.put("point", Point);
                order.put("taka", Taka);
                order.put("Last 4 Digit Number",AccountNum);
                order.put("ok","0");
                order.put("applied","0");

                if (num.isEmpty()) {
                    binding.accountNum.setError("Account Number is required");
                    binding.accountNum.requestFocus();

                }

                if (num.isEmpty()) {
                    binding.accountNum.setError("Account Number is required");
                    binding.accountNum.requestFocus();

                }


                if (num.length() < 4) {
                    binding.accountNum.setError("Please Provide Last 4 digit Account Number");
                } else {
                    database.getReference()
                            .child("order")
                            .child(auth.getCurrentUser().getUid())
                            .push()
                            .setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            /*database.getReference()
                                    .child("Users")
                                    .child(auth.getUid())
                                    .child("coins")
                                    .setValue(UserPoint + Point);*/
                            Toast.makeText(OrderActivity.this, "Order send success, please wait for admin confirmation.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }


            }
        });


    }
}