/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 *  if your need any help knock this number +8801776254584 whatsapp
 */

package com.adda.datingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivityInComingVdBinding;
import com.adda.datingapp.network.ApiClient;
import com.adda.datingapp.network.ApiService;
import com.adda.datingapp.utilse.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InComingVdActivity extends AppCompatActivity {

    ActivityInComingVdBinding binding;
    private String meetingType = null;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityInComingVdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
        String name = getIntent().getStringExtra(Constants.KEY_NAME);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        if (meetingType != null) {
            if (meetingType.equals("video")) {
                binding.rejected.setImageResource(R.drawable.videocall);
                binding.Accepted.setImageResource(R.drawable.videocall);
                binding.textVie.setText("Incoming Video call");

            } else {
                binding.rejected.setImageResource(R.drawable.phone_call);
                binding.Accepted.setImageResource(R.drawable.phone_call);
                binding.textVie.setText("Incoming Audio call");

            }
        }

        if (name != null) {
            binding.UserTexInComin.setText(name.substring(0, 1));
            binding.usernameIncoming.setText(name);
        }

        binding.Accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));

            }
        });

        binding.rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN));
            }
        });


    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);

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
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                        try {
                            URL serverURl = new URL("https://meet.jit.si");

                            JitsiMeetConferenceOptions.Builder builder= new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURl);
                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
                            if (meetingType.equals("audio")){
                                builder.setVideoMuted(true);
                                builder.setAudioOnly(true);
                            }

                            JitsiMeetActivity.launch(InComingVdActivity.this, builder.build());
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(InComingVdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(InComingVdActivity.this, "Call Cancel", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                } else {
                    Toast.makeText(InComingVdActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();

                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(InComingVdActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(context, "Call Receive", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer = MediaPlayer.create(this, R.raw.call_ringtone);
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
    public void onPause() {

        super.onPause();

        mediaPlayer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("presence").child(FirebaseAuth.getInstance().getUid()).setValue("Online");
    }


}