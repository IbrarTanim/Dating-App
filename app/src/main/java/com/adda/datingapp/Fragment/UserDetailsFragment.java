package com.adda.datingapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adda.datingapp.R;
import com.adda.datingapp.activity.ChatsActivity;
import com.adda.datingapp.databinding.FragmentPartnerDetailsBinding;
import com.adda.datingapp.databinding.FragmentUserDetailsBinding;
import com.adda.datingapp.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserDetailsFragment extends Fragment {

    private Context context;
    private FragmentUserDetailsBinding binding;
    private FirebaseDatabase database;
    private User user;

    private String userId = null;

    public UserDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false);

        //get arguments
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()){
            userId = bundle.getString("user_id");
        }

        //get database values
        database.getReference()
                .child("Users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()&& snapshot.getValue() !=null){
                            user = snapshot.getValue(User.class);

                            if (user.getProfile() != null){
                                Glide.with(context)
                                        .load(user.getProfile())
                                        .placeholder(R.drawable.plasholder)
                                        .into(binding.userProfile);
                            }

                            if (user.getName() != null){
                                binding.userName.setText(user.getName());
                            }

                            if (user.getUserBio() != null){
                                binding.userBio.setText(user.getUserBio());
                            }

                            //initialize views
                            initialize();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }

    private void initialize() {
        binding.chatButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatsActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("image", user.getProfile());
            intent.putExtra("uid", user.getUid());
            intent.putExtra("token", user.getToken());
            intent.putExtra("user", user);
            context.startActivity(intent);
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}