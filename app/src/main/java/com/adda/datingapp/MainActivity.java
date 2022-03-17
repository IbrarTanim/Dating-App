package com.adda.datingapp;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.adda.datingapp.Fragment.ChatsFragment;
import com.adda.datingapp.Fragment.HomeMainFragment;
import com.adda.datingapp.Fragment.PointsWalletFragment;
import com.adda.datingapp.Fragment.MainProfileFragment;
import com.adda.datingapp.activity.LoginActivity;
import com.adda.datingapp.databinding.ActivityMainBinding;

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


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private PreferenceManager preferenceManager;

    private  int REQUEST_CODE_BATTERY_OPTIMIZATIONS=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());



        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.FragmentContent, new HomeMainFragment());
        transaction.commit();

//        checkForBatteryOptimizations();


        binding.bottomTop.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.homeMenu:
                        transaction.replace(R.id.FragmentContent, new HomeMainFragment());
                        break;
                    case R.id.chatMenu:
                        if (auth.getCurrentUser() != null) {
                            transaction.replace(R.id.FragmentContent, new ChatsFragment());
                        } else {
                            startLogin();
                        }
                        break;
                    case R.id.pointsMenu:
                        if (auth.getCurrentUser() != null) {
                            transaction.replace(R.id.FragmentContent, new PointsWalletFragment());
                        } else {
                            startLogin();
                        }
                        break;
                    case R.id.profileMenu:
                        if (auth.getCurrentUser() != null) {
                            transaction.replace(R.id.FragmentContent, new MainProfileFragment());

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
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//
//    private void checkForBatteryOptimizations(){
//        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.M){
//            PowerManager powerManager=(PowerManager) getSystemService(POWER_SERVICE);
//            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())){
//                AlertDialog.Builder builder= new AlertDialog.Builder(this);
//                builder.setTitle("Warning");
//                builder.setMessage("If you want Realtime Phone call so disable the Battery optimization");
//                builder.setPositiveButton("Disable", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                        startActivityForResult(intent,REQUEST_CODE_BATTERY_OPTIMIZATIONS);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.create().show();
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_BATTERY_OPTIMIZATIONS){
//            checkForBatteryOptimizations();
//        }
//    }
//

}