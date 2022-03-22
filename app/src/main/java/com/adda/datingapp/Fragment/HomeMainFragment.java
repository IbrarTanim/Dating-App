package com.adda.datingapp.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;


import com.adda.datingapp.MainActivity;
import com.adda.datingapp.R;
import com.adda.datingapp.activity.LoginActivity;
import com.adda.datingapp.adapter.UserAdapter;
import com.adda.datingapp.databinding.CustomPopupBinding;
import com.adda.datingapp.databinding.FragmentHomeBinding;


import com.adda.datingapp.listeners.ClickListener;
import com.adda.datingapp.model.User;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HomeMainFragment extends Fragment {

    ArrayList<User> list;
    FirebaseDatabase database;
    FirebaseAuth auth;
    Dialog popupDialog;
    Context context;


    public HomeMainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        popupDialog = new Dialog(getContext());


    }

    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        binding.getPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (auth.getCurrentUser() != null){
                    PointsWalletFragment pointsWalletFragment = new PointsWalletFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.FragmentContent, pointsWalletFragment);
                    transaction.commit();
                }else {
                    startLogin();
                    startLogin();
                }
            }
        });


        list = new ArrayList<>();

        UserAdapter adapter = new UserAdapter(list, getContext(), ((view, position) -> {

            if (auth.getCurrentUser() != null){
                if (list != null && !list.isEmpty()) {
                    User user = list.get(position);
                    String userId = user.getUid();

                    PartnerDetailsFragment partnerDetailsFragment = new PartnerDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", userId);
                    partnerDetailsFragment.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.FragmentContent, partnerDetailsFragment);
                    transaction.commit();
                }
            }else {
                startLogin();
            }

        }));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.viewProfileRv.setLayoutManager(gridLayoutManager);
        binding.viewProfileRv.setAdapter(adapter);

        database.getReference().child("Partner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (auth.getCurrentUser() != null) {

            database.getReference()
                    .child("Users")
                    .child(auth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.getValue() != null) {
                                User user = snapshot.getValue(User.class);
                                long coins = (long) user.getCoins();
                                binding.usercoin.setText("My Points: " + coins);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


            FirebaseMessaging.getInstance()
                    .getToken()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String token) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("token", token);
                            database.getReference()
                                    .child("Users")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(map);
                            //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        }
                    });

        }


        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            database.getReference().child("presence").child(auth.getUid()).setValue("online");
        }
    }

    @Override
    public void onPause() {
        if (auth.getCurrentUser() != null) {
            database.getReference().child("presence").child(auth.getUid()).setValue("offline");
        }
        super.onPause();
    }

    public void startLogin() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.login_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button cancelBTN = dialog.findViewById(R.id.canselBTN);
        Button loginBTN = dialog.findViewById(R.id.loginBTN);

        cancelBTN.setOnClickListener(view -> {
            dialog.dismiss();
        });

        loginBTN.setOnClickListener(view -> {
            dialog.dismiss();
            Intent loginIntent = new Intent(context, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        });
        dialog.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}