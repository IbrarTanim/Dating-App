package com.adda.datingapp.activity;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.adda.datingapp.MainActivity;
import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivityLoginBinding;
import com.adda.datingapp.utilse.Constants;
import com.adda.datingapp.utilse.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityLoginBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        preferenceManager = new PreferenceManager(getApplicationContext());


        dialog = new ProgressDialog(this);
        dialog.setMessage("login in...");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser()!=null){
            checkUserAcceseLevel(auth.getUid());

            finishAffinity();
        }




        binding.Login.setOnClickListener(this);


        binding.SingupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                dialog.dismiss();
            }
        });

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Login:
                login();
                break;
        }

    }

    private void login() {
        String emails = binding.EmailEt.getText().toString().trim();
        String passwords = binding.passwordEt.getText().toString().trim();

        if (emails.isEmpty()) {
            binding.EmailEt.setError("Please Provide Email");
            binding.EmailEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emails).matches()) {
            binding.EmailEt.setError("Please Provide valid email");
            binding.EmailEt.requestFocus();
            return;
        }


        if (passwords.isEmpty()) {
            binding.passwordEt.setError("Please Provide Password");
            binding.passwordEt.requestFocus();
            return;
        }

        binding.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String email = binding.EmailEt.getText().toString(), password = binding.passwordEt.getText().toString();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.show();
                           checkUserAcceseLevel(task.getResult().getUser().getUid());
                           finishAffinity();

                        }else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
    }

    private void checkUserAcceseLevel(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(uid)
                .child("phone")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                      try {
                          String phone=snapshot.getValue(String.class);

                        if (Objects.equals(phone, "")){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finishAffinity();


                        }else {
                            startActivity(new Intent(LoginActivity.this, PartnerMainActivity.class));
                            finish();
                        }   }catch (Exception e){
                          e.printStackTrace();
                      }

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }
}