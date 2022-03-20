package com.adda.datingapp.Fragment;
/*
 * Created by  MD.Masud Raj on 2/24/22 1:06AM
 *  Copyright (c) 2022 . All rights reserved.
 * if your need any help knock this number +8801776254584 whatsapp
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adda.datingapp.model.User;
import com.adda.datingapp.R;
import com.adda.datingapp.activity.LoginActivity;
import com.adda.datingapp.databinding.FragmentProfileBinding;
import com.adda.datingapp.utilse.Constants;
import com.adda.datingapp.utilse.PreferenceManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MainProfileFragment extends Fragment {

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    PreferenceManager preferenceManager;


    public MainProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        preferenceManager = new PreferenceManager(getContext());


        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Change Profile...");
        dialog.setCancelable(false);

    }

    FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        //get Data
        getAllData();

        binding.GetCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointsWalletFragment pointsWalletFragment = new PointsWalletFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContent, pointsWalletFragment);
                transaction.commit();
            }
        });


        binding.changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 8);
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();

                Intent logOutIntent = new Intent(getActivity(), LoginActivity.class);
                logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logOutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logOutIntent);
            }
        });


        binding.ubio.setOnClickListener(view -> {

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.edit_bio_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(false);

            Button saveBTN = dialog.findViewById(R.id.SaveBTN);
            Button cancelBTN = dialog.findViewById(R.id.canselBTN);
            EditText editBio = dialog.findViewById(R.id.bioEt);

            cancelBTN.setOnClickListener(view1 -> {
                dialog.dismiss();
            });

            saveBTN.setOnClickListener(view1 -> {
                String bioText = String.valueOf(editBio.getText());

                if (!bioText.isEmpty()) {
                    database.getReference()
                            .child("Users")
                            .child(auth.getUid())
                            .child("userBio")
                            .setValue(bioText)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    getAllData();
                                }
                            });
                    dialog.dismiss();
                }
            });

            dialog.show();

        });


        return binding.getRoot();
    }

    private void getAllData() {

        database.getReference()
                .child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            User user = snapshot.getValue(User.class);
                            long coins = (long) user.getCoins();
                            binding.userPoint.setText("My Points: " + coins);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    User userModel = snapshot.getValue(User.class);
                    Glide.with(getContext())
                            .load(userModel.getProfile())
                            .placeholder(R.drawable.plasholder)
                            .into(binding.userProfile);

                    binding.uname.setText(userModel.getName());

                    if (userModel.getUserBio() != null && !userModel.getUserBio().isEmpty()) {
                        binding.ubio.setText(userModel.getUserBio());
                    } else {
                        binding.ubio.setText("Edit Bio");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("presence").child(FirebaseAuth.getInstance().getUid()).setValue("Online");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 8) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    binding.userProfile.setImageURI(uri);
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final StorageReference reference = storage.getReference()
                            .child("user_photo")
                            .child(uid);
                    dialog.show();
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Profile Saved", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(@NonNull @NotNull Uri uri) {
                                    database.getReference()
                                            .child("Users")
                                            .child(auth.getUid())
                                            .child("Profile")
                                            .setValue(uri.toString());


                                }
                            });
                        }
                    });

                }
            }
        }
    }
}