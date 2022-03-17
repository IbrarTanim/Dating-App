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
import android.widget.CompoundButton;
import android.widget.Toast;

import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivityWithDrawBinding;
import com.adda.datingapp.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class WithDrawActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityWithDrawBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String Name;
    String Email;
    double PartnerPoint;
    User user;
    double partnerAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithDrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        binding.confirm.setOnClickListener(WithDrawActivity.this);


        binding.bakashBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    binding.roketBox.setChecked(false);
                    binding.RocketEDT.setVisibility(View.GONE);
                    binding.RocketEDT.setEnabled(false);
                    binding.NagatBox.setChecked(false);
                    binding.NagadEDT.setEnabled(false);
                    binding.NagadEDT.setVisibility(View.GONE);
                    binding.bkashEdt.setEnabled(true);
                    binding.bkashEdt.setVisibility(View.VISIBLE);

                }
            }
        });
        binding.roketBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    binding.bakashBox.setChecked(false);
                    binding.bkashEdt.setEnabled(false);
                    binding.bkashEdt.setVisibility(View.GONE);
                    binding.NagatBox.setChecked(false);
                    binding.NagadEDT.setEnabled(false);
                    binding.NagadEDT.setVisibility(View.GONE);
                    binding.RocketEDT.setVisibility(View.VISIBLE);
                    binding.RocketEDT.setEnabled(true);


                }
            }
        });
        binding.NagatBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    binding.roketBox.setChecked(false);
                    binding.RocketEDT.setVisibility(View.GONE);
                    binding.RocketEDT.setEnabled(false);
                    binding.bakashBox.setChecked(false);
                    binding.bkashEdt.setEnabled(false);
                    binding.bkashEdt.setVisibility(View.GONE);
                    binding.NagadEDT.setEnabled(true);
                    binding.NagadEDT.setVisibility(View.VISIBLE);

                }
            }
        });

        database.getReference()
                .child("Partner")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(User.class);
                            Name = user.getName();
                            Email = user.getEmail();
                            PartnerPoint = user.getCoins();


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                withdraw();
                break;
        }
    }

    public void withdraw() {
        String amountEt = binding.amount.getText().toString().trim();
        String BkashEt = binding.bkashEdt.getText().toString().trim();
        String nagadEt = binding.NagadEDT.getText().toString().trim();
        String rocketEt = binding.RocketEDT.getText().toString().trim();


        if (amountEt.isEmpty()) {
            binding.amount.setError("Amount is required");
            binding.amount.requestFocus();
            return;
        }


        if (!(binding.bakashBox.isChecked() || binding.roketBox.isChecked() || binding.NagatBox.isChecked())) {
            Toast.makeText(WithDrawActivity.this, "Please Select Account Type", Toast.LENGTH_SHORT).show();
            binding.bakashBox.setError("Please Select Account Type");
            binding.roketBox.setError("Please Select Account Type");
            binding.NagatBox.setError("Please Select Account Type");
            return;
        } else {
            String Amount = binding.amount.getText().toString();
            String Bkash = binding.bkashEdt.getText().toString();
            String Nagad = binding.NagadEDT.getText().toString();
            String Rocket = binding.RocketEDT.getText().toString();

            if (user.getCoins() > 499) {
                if (!(binding.roketBox.isChecked() || binding.NagatBox.isChecked())) {

                    if (BkashEt.isEmpty()) {
                        binding.bkashEdt.setError("Account Number is required");
                        binding.bkashEdt.requestFocus();
                        return;
                    }


                    HashMap<String, Object> withdraw = new HashMap<>();
                    withdraw.put("Amount", Amount);
                    withdraw.put("Bkash Personal", Bkash);
                    withdraw.put("name", Name);
                    withdraw.put("Email", Email);
                    withdraw.put("Phone", user.getPhone());
                    binding.bkashEdt.setVisibility(View.VISIBLE);
                    binding.bkashEdt.setEnabled(true);


                    database.getReference()
                            .child("withdraw")
                            .child(auth.getUid())
                            .push()
                            .setValue(withdraw).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            partnerAmount = Double.parseDouble(Amount);
                            database.getReference()
                                    .child("Partner")
                                    .child(auth.getUid())
                                    .child("coins")
                                    .setValue(PartnerPoint - partnerAmount);
                            Toast.makeText(WithDrawActivity.this, "WithDraw send Success ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            } else {
                Toast.makeText(WithDrawActivity.this, "আপনার  পর্যাবতো পরিমানে ব্যালেন্স নেই ৫০০ পয়েন্ট হলে উইথড্র  করতে পারবেন ", Toast.LENGTH_SHORT).show();
            }
        }


        if (!(binding.bakashBox.isChecked() || binding.roketBox.isChecked() || binding.NagatBox.isChecked())) {
            Toast.makeText(WithDrawActivity.this, "Please Select Account Type", Toast.LENGTH_SHORT).show();
            binding.bakashBox.setError("Please Select Account Type");
            binding.roketBox.setError("Please Select Account Type");
            binding.NagatBox.setError("Please Select Account Type");
            return;
        } else {
            String Amount = binding.amount.getText().toString();
            String Bkash = binding.bkashEdt.getText().toString();
            String Nagad = binding.NagadEDT.getText().toString();
            String Rocket = binding.RocketEDT.getText().toString();

            if (user.getCoins() > 499) {
                if (!(binding.NagatBox.isChecked() || binding.bakashBox.isChecked())) {

                    if (rocketEt.isEmpty()) {
                        binding.RocketEDT.setError("Account Number is required");
                        binding.RocketEDT.requestFocus();
                        return;
                    }


                    HashMap<String, Object> withdraw = new HashMap<>();
                    withdraw.put("Amount", Amount);
                    withdraw.put("Rocket Personal", Rocket);
                    withdraw.put("name", Name);
                    withdraw.put("Email", Email);
                    withdraw.put("Phone", user.getPhone());
                    binding.RocketEDT.setVisibility(View.VISIBLE);
                    binding.RocketEDT.setEnabled(true);


                    database.getReference()
                            .child("withdraw")
                            .child(auth.getUid())
                            .push()
                            .setValue(withdraw).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            partnerAmount = Double.parseDouble(Amount);
                            database.getReference()
                                    .child("Partner")
                                    .child(auth.getUid())
                                    .child("coins")
                                    .setValue(PartnerPoint - partnerAmount);
                            Toast.makeText(WithDrawActivity.this, "WithDraw send Success ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            } else {
                Toast.makeText(WithDrawActivity.this, "আপনার  পর্যাবতো পরিমানে ব্যালেন্স নেই ৫০০ পয়েন্ট হলে উইথড্র  করতে পারবেন ", Toast.LENGTH_SHORT).show();
            }
        }


        if (!(binding.bakashBox.isChecked() || binding.roketBox.isChecked() || binding.NagatBox.isChecked())) {
            Toast.makeText(WithDrawActivity.this, "Please Select Account Type", Toast.LENGTH_SHORT).show();
            binding.bakashBox.setError("Please Select Account Type");
            binding.roketBox.setError("Please Select Account Type");
            binding.NagatBox.setError("Please Select Account Type");
            return;
        } else {
            String Amount = binding.amount.getText().toString();
            String Bkash = binding.bkashEdt.getText().toString();
            String Nagad = binding.NagadEDT.getText().toString();
            String Rocket = binding.RocketEDT.getText().toString();

            if (user.getCoins() > 99) {
                if (!(binding.roketBox.isChecked() || binding.bakashBox.isChecked())) {

                    if (nagadEt.isEmpty()) {
                        binding.NagadEDT.setError("Account Number is required");
                        binding.NagadEDT.requestFocus();
                        return;
                    }


                    HashMap<String, Object> withdraw = new HashMap<>();
                    withdraw.put("Amount", Amount);
                    withdraw.put("Nagad Personal", Nagad);
                    withdraw.put("name", Name);
                    withdraw.put("Email", Email);
                    withdraw.put("Phone", user.getPhone());
                    binding.NagadEDT.setVisibility(View.VISIBLE);
                    binding.NagadEDT.setEnabled(true);


                    database.getReference()
                            .child("withdraw")
                            .child(auth.getUid())
                            .push()
                            .setValue(withdraw).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            partnerAmount = Double.parseDouble(Amount);
                            database.getReference()
                                    .child("Partner")
                                    .child(auth.getUid())
                                    .child("coins")
                                    .setValue(PartnerPoint - partnerAmount);
                            Toast.makeText(WithDrawActivity.this, "WithDraw send Success আপনার একাউন্টে ১২ ঘন্টার মধ্যেই টাকা পাঠিয়ে দেওয়া হবে।", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            } else {
                Toast.makeText(WithDrawActivity.this, "আপনার পর্যাপ্ত পরিমান ব্যালেন্স নেই। ১০০ পয়েন্ট হলে উত্তোলন করতে পারবেন।", Toast.LENGTH_SHORT).show();
            }
        }
    }
}