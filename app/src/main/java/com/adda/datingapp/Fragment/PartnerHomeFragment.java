package com.adda.datingapp.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adda.datingapp.R;
import com.adda.datingapp.adapter.UserAdapter;
import com.adda.datingapp.databinding.FragmentPartnerHomeBinding;

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


public class PartnerHomeFragment extends Fragment {


    ArrayList<User> list;
    FirebaseDatabase database;
    FirebaseAuth auth;
    long coins = 50;

    public PartnerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        String token;

    }


    FragmentPartnerHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPartnerHomeBinding.inflate(inflater, container, false);


        list = new ArrayList<>();

        UserAdapter adapter = new UserAdapter(list, getContext(), (view, position) -> {
            if (list != null && !list.isEmpty()){
                User user = list.get(position);
                String userId = user.getUid();

                UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", userId);
                userDetailsFragment.setArguments(bundle);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.partnerContent, userDetailsFragment);
                transaction.commit();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.userlistRV.setLayoutManager(gridLayoutManager);
        binding.userlistRV.setAdapter(adapter);


        database.getReference()
                .child("Partner")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {
                            User user = snapshot.getValue(User.class);
                            long coins= (long) user.getCoins();
                            binding.PartnerBlance.setText("My Points: "+coins);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.<User>getValue(User.class);
                    list.add(user);

                }
                adapter.notifyDataSetChanged();

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
                                .child("Partner")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        database.getReference().child("presence").child(auth.getUid()).setValue("online");
    }

    @Override
    public void onPause() {
        database.getReference().child("presence").child(auth.getUid()).setValue("offline");
        super.onPause();
    }


}