/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 *  if your need any help knock this number +8801776254584 whatsapp
 */

package com.adda.datingapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivityOutComingVdBinding;
import com.adda.datingapp.model.User;
import com.adda.datingapp.network.ApiClient;
import com.adda.datingapp.network.ApiService;
import com.adda.datingapp.utilse.Constants;
import com.adda.datingapp.utilse.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.protobuf.StringValueOrBuilder;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OutComingVdActivity extends AppCompatActivity {

    ActivityOutComingVdBinding binding;
    private String inviterToken = null;
    private PreferenceManager preferenceManager;
    private String meetingRoom = null;
    private String meetingType = null;
    MediaPlayer mediaPlayer;
    User user;
    int count = 0;
    boolean isActive;
    boolean off;
    long userCoin = 50;
    double coins = 0;
    double partnerCoin = 0;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String phone;
    String receiverUid;
    JitsiMeetConferenceOptions.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutComingVdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        meetingType = getIntent().getStringExtra("type");
        receiverUid = getIntent().getStringExtra("receiverUid");
        user = (User) getIntent().getSerializableExtra("user");


        partnerCoin = user.getCoins();


        cancelCall();

        database.getReference()
                .child("Users")
                .child(auth.getUid())
                .child("phone")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            phone = snapshot.getValue(String.class);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(User.class);
                            if (user.getPhone().equals("")) {
                                coins = user.getCoins();
                                userCoin = (long) user.getCoins();
                            } else {
                                finishAndRemoveTask();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if (meetingType != null) {
            if (meetingType.equals("video")) {
                binding.callends.setImageResource(R.drawable.videocall);
                binding.calltypeText.setText("Ringing...");

            } else {
                binding.callends.setImageResource(R.drawable.phone_call);
                binding.calltypeText.setText("Ringing...");

            }
        }

        if (user != null) {
            binding.Usertex.setText(user.getName().substring(0, 1));
            binding.username.setText(user.getName());
            binding.emails.setText(user.getEmail());
        }

        binding.callends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    cancelInvitation(user.getToken());
                }
            }
        });

        FirebaseMessaging.getInstance()
                .getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    inviterToken = task.getResult();
                    if (meetingType != null && user != null) {
                        initiateMeeting(meetingType, user.getToken());
                    }
                }

            }
        });
    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = user.getUid() + "-" +
                    UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);


            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        Toast.makeText(OutComingVdActivity.this, "Calling", Toast.LENGTH_SHORT).show();
                    } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutComingVdActivity.this, "Call Cancel", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(OutComingVdActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutComingVdActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void cancelInvitation(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {

                if (userCoin > 4) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                        isActive = true;
                        content();

                        try {
                            URL serverURl = new URL("https://meet.jit.si");
                            builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURl);
                            builder.setRoom(meetingRoom);
                            if (meetingType.equals("audio")) {
                                builder.setVideoMuted(true);
                                builder.setAudioOnly(true);
                            }

                            JitsiMeetActivity.launch(OutComingVdActivity.this, builder.build());

                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            isActive = false;
                            finish();

                        }
                    } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                        Toast.makeText(context, "Call Cancel", Toast.LENGTH_SHORT).show();
                        isActive = false;
                        finish();
                    }
                } else {
                    Toast.makeText(context, "Please Check your Balance", Toast.LENGTH_SHORT).show();
                }
            } else {
                isActive = false;
            }
        }
    };




    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer = MediaPlayer.create(this, R.raw.ringing);
        mediaPlayer.start();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive=false;
        FirebaseDatabase.getInstance().getReference().child("presence").child(FirebaseAuth.getInstance().getUid()).setValue("Online");
    }

    @Override
    public void onPause() {
        super.onPause();

        mediaPlayer.stop();
    }

    private void content() {
        count++;

        if (isActive) {
            refresh(15000);

        }

        if (count > 1) {
            coins = coins - 5;
            if (Objects.equals(phone, "")) {
                database.getReference()
                        .child("Users")
                        .child(auth.getCurrentUser().getUid())
                        .child("coins")
                        .setValue(coins);


                partnerCoin = partnerCoin + 3.5;
                database.getReference()
                        .child("Partner")
                        .child(receiverUid)
                        .child("coins")
                        .setValue(partnerCoin);
            }
        }
    }

    private void refresh(int millisecond) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
        handler.postDelayed(runnable, millisecond);
    }


    private void cancelCall() {
        if (userCoin > 4) {
            isActive = false;
            try {
                builder.setAudioMuted(true);

                builder.setVideoMuted(true);
            } catch (Exception e) {
                e.getMessage();
            }

        } else {
            isActive = true;
        }
    }

}