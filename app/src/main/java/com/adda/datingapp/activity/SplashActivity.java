package com.adda.datingapp.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;


import com.adda.datingapp.MainActivity;
import com.adda.datingapp.databinding.ActivitySplashBinding;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    SplashActivity splashActivity;
    Context context;
    AppUpdateManager UpdateManager;
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        context = splashActivity = this;
        UpdateManager = AppUpdateManagerFactory.create(context);
        UpdateApp();



    }

    @Override
    protected void onResume() {
        super.onResume();
        splashActivity = this;

        UpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    UpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, IMMEDIATE, splashActivity, 101);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void HomeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null){
                    checkUserAcceseLevel(auth.getUid());
                }else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 2000);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void UpdateApp() {
        try {
            Task<AppUpdateInfo> appUpdateInfoTask = UpdateManager.getAppUpdateInfo();
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        UpdateManager.startUpdateFlowForResult(
                                appUpdateInfo, IMMEDIATE, splashActivity, 101);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    HomeScreen();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                HomeScreen();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode != RESULT_OK) {
                HomeScreen();
            } else {
                HomeScreen();
            }
        }
    }


    private void checkUserAcceseLevel(String uid) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(uid)
                .child("phone")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            String phone = snapshot.getValue(String.class);

                            if (Objects.equals(phone, "")) {
                                Intent mainUserIntent = new Intent(SplashActivity.this, MainActivity.class);
                                mainUserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                mainUserIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mainUserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainUserIntent);
                            } else {

                                FirebaseDatabase.getInstance().getReference()
                                        .child("Partner")
                                        .child(uid)
                                        .child("approved")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    if (snapshot.getValue(String.class).equals("1")){
                                                        Intent mainPartnerIntent = new Intent(SplashActivity.this, PartnerMainActivity.class);
                                                        mainPartnerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        mainPartnerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        mainPartnerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(mainPartnerIntent);
                                                    }else {
                                                        FirebaseAuth auth = FirebaseAuth.getInstance();
                                                        auth.signOut();
                                                        Toast.makeText(SplashActivity.this, "Your account not approved yet!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }catch (Exception e){
                                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                                    auth.signOut();
                                                    Toast.makeText(SplashActivity.this, "Your account not approved yet!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}