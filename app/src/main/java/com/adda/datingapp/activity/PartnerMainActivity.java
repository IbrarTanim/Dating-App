package com.adda.datingapp.activity;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.adda.datingapp.Fragment.ChatsFragment;
import com.adda.datingapp.Fragment.PartnerChatsFragment;
import com.adda.datingapp.Fragment.PartnerHomeFragment;
import com.adda.datingapp.Fragment.PartnerProfileFragment;
import com.adda.datingapp.MainActivity;
import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivityMainPartnerBinding;
import com.adda.datingapp.utilse.Constants;
import com.adda.datingapp.utilse.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.jar.Pack200;

public class PartnerMainActivity extends AppCompatActivity {

    ActivityMainPartnerBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private PreferenceManager preferenceManager;
    private  int REQUEST_CODE_BATTERY_OPTIMIZATIONS=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPartnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.partnerContent, new PartnerHomeFragment());
        transaction.commit();

//        checkForBatteryOptimizations();






        binding.partnerBottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.partnerHomeMenu:
                        if (auth.getCurrentUser() != null) {
                            transaction.replace(R.id.partnerContent, new PartnerHomeFragment());
                        } else {
                            startLogin();
                        }
                        break;
                    case R.id.partnerChatMenu:
                        if (auth.getCurrentUser() != null) {
                            transaction.replace(R.id.partnerContent, new PartnerChatsFragment());
                        } else {
                            startLogin();
                        }

                        break;
                    case R.id.partnerProfileMenu:
                        if (auth.getCurrentUser() != null) {
                            transaction.replace(R.id.partnerContent, new PartnerProfileFragment());
                        } else {
                            startLogin();
                        }

                        break;
                }
                transaction.commit();
                return true;

            }
        });


    }




    public void startLogin() {
        startActivity(new Intent(PartnerMainActivity.this, LoginActivity.class));

    }





}