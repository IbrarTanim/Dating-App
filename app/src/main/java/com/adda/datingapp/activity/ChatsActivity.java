package com.adda.datingapp.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.adda.datingapp.Fragment.PointsWalletFragment;
import com.adda.datingapp.adapter.MessagesAdapter;

import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivityChatsBinding;
import com.adda.datingapp.model.MessageModel;
import com.adda.datingapp.model.User;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatsActivity extends AppCompatActivity {

    ActivityChatsBinding binding;
    ArrayList<MessageModel> messages;
    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderUid;
    String receiverUid;
    String name;
    String token;
    String profile;
    double coins = 0;
    MessagesAdapter messagesAdapter;
    private int requestCode = 1;
    User callUser;
    boolean isActive;
    double partnerCoin = 0;
    User user;
    long userCoin = 50;
    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        name = getIntent().getStringExtra("name");
        profile = getIntent().getStringExtra("image");
        token = getIntent().getStringExtra("token");

        callUser = (User) getIntent().getSerializableExtra("user");


        receiverUid = getIntent().getStringExtra("uid");

        senderUid = FirebaseAuth.getInstance().getUid();

        partnerCoin = callUser.getCoins();


        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;


        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending image...");
        dialog.setCancelable(false);


        binding.videocalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userCoin > 19) {
                    Intent intent = new Intent(ChatsActivity.this, OutComingVdActivity.class);
                    intent.putExtra("user", callUser);
                    intent.putExtra("type", "video");
                    intent.putExtra("receiverUid", receiverUid);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChatsActivity.this, "ফোন করার জন্য আপনার পর্যাপ্ত পয়েন্ট নেই।প্লিজ পয়েন্ট রিচার্জ করুন।", Toast.LENGTH_SHORT).show();


                }

            }
        });


        binding.audiocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userCoin > 19) {
                    Intent intent = new Intent(ChatsActivity.this, OutComingVdActivity.class);
                    intent.putExtra("user", callUser);
                    intent.putExtra("type", "audio");
                    intent.putExtra("receiverUid", receiverUid);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChatsActivity.this, "ফোন করার জন্য আপনার পর্যাপ্ত পয়েন্ট নেই।প্লিজ পয়েন্ট রিচার্জ করুন।", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        database.getReference()
                .child("Users")
                .child(auth.getUid())
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


        database.getReference().child("presence").child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()) {
                        binding.status.setText(status);
                        binding.status.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.name.setText(name);
        Glide.with(ChatsActivity.this)
                .load(profile)
                .placeholder(R.drawable.plasholder)
                .into(binding.profile);

        binding.backMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        messages = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesAdapter = new MessagesAdapter(messages, this);
        layoutManager.setStackFromEnd(true);
        layoutManager.isSmoothScrolling();
        binding.massegeRV.setLayoutManager(layoutManager);
        binding.massegeRV.setAdapter(messagesAdapter);
        binding.massegeRV.scrollToPosition(messages.size()-1);

        database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("message")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                            String snapshotId = dataSnapshot.getKey();
                            if (messageModel.getSenderId().equals(receiverUid)){
                                database.getReference()
                                        .child("chats")
                                        .child(senderRoom)
                                        .child("message")
                                        .child(snapshotId)
                                        .child("seenStatus")
                                        .setValue("1");
                            }
                            messages.add(messageModel);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String messageTxt = binding.messageBox.getText().toString();
                if (!messageTxt.isEmpty()) {
                    binding.sendBtn.setEnabled(true);


                } else {
                    binding.sendBtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStopTyping, 1000);


            }

            Runnable userStopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("online");


                }
            };


        });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userCoin > 0.50) {
                    if (Objects.equals(phone, "")) {
                        coins = coins - 0.50;
                        database.getReference()
                                .child("Users")
                                .child(auth.getCurrentUser().getUid())
                                .child("coins")
                                .setValue(coins);


                        partnerCoin = partnerCoin + 0.40;
                        database.getReference()
                                .child("Partner")
                                .child(receiverUid)
                                .child("coins")
                                .setValue(partnerCoin);
                    }


                    String messageTxt = binding.messageBox.getText().toString();
                    binding.messageBox.setText("");
                    Date date = new Date();
                    MessageModel messageModel = new MessageModel(messageTxt, senderUid, date.getTime());


                    HashMap<String, Object> lastMessageObject = new HashMap<>();
                    lastMessageObject.put("lastMsg", messageModel.getMessage());
                    lastMessageObject.put("lastMsgTime", date.getTime());

                    database.getReference().child("chats").child(senderRoom).updateChildren(lastMessageObject);
                    database.getReference().child("chats").child(receiverRoom).updateChildren(lastMessageObject);


                    database.getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("message")
                            .push()
                            .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("message")
                                    .push()
                                    .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    sendNotification(name, messageModel.getMessage(), token);

                                }
                            });

                            HashMap<String, Object> lastMessageObject = new HashMap<>();
                            lastMessageObject.put("lastMsg", messageModel.getMessage());
                            lastMessageObject.put("lastMsgTime", date.getTime());

                            database.getReference().child("chats").child(senderRoom).updateChildren(lastMessageObject);
                            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMessageObject);

                        }
                    });
                } else {
                    Toast.makeText(ChatsActivity.this, "ম্যাসেজ করার জন্য আপনার পর্যাপ্ত পয়েন্ট নেই।প্লিজ পয়েন্ট রিচার্জ করুন।", Toast.LENGTH_SHORT).show();

                }
            }
        });


        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 21);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    Calendar calendar = Calendar.getInstance();

                    StorageReference reference = storage.getReference()
                            .child("chat_photo")
                            .child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filPath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();
                                        Date date = new Date();
                                        MessageModel messageModel = new MessageModel(messageTxt, senderUid, date.getTime());
                                        binding.messageBox.setText("");
                                        messageModel.setImageUrl(filPath);
                                        messageModel.setMessage("photo");


                                        HashMap<String, Object> lastMessageObject = new HashMap<>();
                                        lastMessageObject.put("lastMsg", messageModel.getMessage());
                                        lastMessageObject.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMessageObject);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMessageObject);


                                        database.getReference()
                                                .child("chats")
                                                .child(senderRoom)
                                                .child("message")
                                                .push()
                                                .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                database.getReference()
                                                        .child("chats")
                                                        .child(receiverRoom)
                                                        .child("message")
                                                        .push()
                                                        .setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });

                                                HashMap<String, Object> lastMessageObject = new HashMap<>();
                                                lastMessageObject.put("lastMsg", messageModel.getMessage());
                                                lastMessageObject.put("lastMsgTime", date.getTime());

                                                database.getReference().child("chats").child(senderRoom).updateChildren(lastMessageObject);
                                                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMessageObject);

                                            }
                                        });
                                    }
                                });
                            }

                        }
                    });
                }
            }

        }
    }


    void sendNotification(String name, String message, String token) {

        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";
            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            Toast.makeText(ChatsActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(ChatsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAAijAKnRE:APA91bGj7bNNCWiq5iNKUGg9pALKYg8L8Lm-pT8UzDk7cyL3x_F39mMV0clVehazsRlBna9nffzQ28Yjm4EMFjngPhuartjkyV8lwiSu7yZQ0z_8hynqBHAjpGdP6PpdCb6syPt2H2C_";
                    map.put("Authorization", key);
                    map.put("Content-Type", "application/json");

                    return map;
                }
            };

            queue.add(request);

        } catch (Exception e) {

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        database.getReference().child("presence").child(auth.getUid()).setValue("online");


    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onStop() {
        database.getReference().child("presence").child(auth.getUid()).setValue("offline");
        isActive = false;

        super.onStop();

        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
    }




}