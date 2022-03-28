package com.adda.datingapp.activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.adda.datingapp.MainActivity;
import com.adda.datingapp.model.User;
import com.adda.datingapp.R;
import com.adda.datingapp.databinding.ActivitySignUpBinding;


import com.adda.datingapp.utilse.Constants;
import com.adda.datingapp.utilse.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private CircleImageView profileImageCIV;
    private Uri profileURI;
    FirebaseStorage storage;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating Account...");
        dialog.setCancelable(false);

        preferenceManager = new PreferenceManager(getApplicationContext());


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        binding.Signup.setOnClickListener(SignUpActivity.this);

        binding.userbex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isChecked()) {
                    binding.partnerbox.setChecked(false);
                    binding.phone.setVisibility(View.GONE);
                    binding.phone.setEnabled(false);
                }
            }
        });

        binding.partnerbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (compoundButton.isChecked()) {
                    binding.userbex.setChecked(false);
                    binding.phone.setVisibility(View.VISIBLE);
                    binding.phone.setEnabled(true);
                }
            }
        });


        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        binding.profileCiv.setOnClickListener( view -> {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 8);

        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Signup:
                Signup();
                break;
        }
    }

    private void Signup() {
        String nameE = binding.Name.getText().toString().trim();
        String emailE = binding.email.getText().toString().trim();
        String conPassE = binding.confrimPassword.getText().toString().trim();
        String passwordE = binding.password.getText().toString().trim();
        String phoneE = binding.phone.getText().toString().trim();


        if (nameE.isEmpty()) {
            binding.Name.setError("Full Name is required");
            binding.Name.requestFocus();
            return;
        }

        if (nameE.length() < 3) {
            binding.Name.setError("Full Name length should be 3 characters");
            binding.Name.requestFocus();
            return;
        }

        if (emailE.isEmpty()) {
            binding.email.setError("Email is required");
            binding.email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailE).matches()) {
            binding.email.setError("Please Provide valid Email");
            binding.email.requestFocus();
            return;
        }


        if (passwordE.isEmpty()) {
            binding.password.setError("Password is required");
            binding.password.requestFocus();
            return;
        }

        if (passwordE.length() < 6) {
            binding.password.setError("Min password length should be 6 characters");
            binding.password.requestFocus();
            return;
        }
        if (conPassE.isEmpty()) {
            binding.confrimPassword.setError("Password is required");
            binding.confrimPassword.requestFocus();
            return;
        }

        if (!conPassE.equals(passwordE)) {
            binding.confrimPassword.setError("Password Could Not be Matched");
            binding.confrimPassword.requestFocus();
            return;
        }

        if (phoneE.isEmpty()) {
            binding.phone.setError("Phone Number is required");
            binding.phone.requestFocus();
            return;
        }

        if (phoneE.length() < 10) {
            binding.phone.setError("Please Provide valid Phone Number");
            binding.phone.requestFocus();
            return;
        }

        if (profileURI == null){
            Toast.makeText(SignUpActivity.this, "Please provide profile picture to create account.", Toast.LENGTH_SHORT).show();
            return;
        }


        String Name = binding.Name.getText().toString(), Email = binding.email.getText().toString(),
                ConfirmPassword = binding.confrimPassword.getText().toString(),
                Password = binding.password.getText().toString(), phone = binding.phone.getText().toString();


        dialog.show();
        if (!(binding.partnerbox.isChecked() || binding.userbex.isChecked())) {
            Toast.makeText(this, "Please Select Account Type", Toast.LENGTH_SHORT).show();
            binding.userbex.setError("Please Select Account Type");
            binding.partnerbox.setError("Please Select Account Type");
            dialog.dismiss();
            return;
        } else {
            auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    dialog.show();
                    if (!(binding.partnerbox.isChecked())) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            String uid = task.getResult().getUser().getUid();
                            User userModel = new User(uid, Name, Email, Password, ConfirmPassword, phone, 0, 50);
                            database.getReference()
                                    .child("Users")
                                    .child(uid)
                                    .setValue(userModel);

                            savePhoto(uid, "Users", "user_photo");

                            preferenceManager.putString(Constants.KEY_NAME, binding.Name.getText().toString());
                            preferenceManager.putString(Constants.KEY_EMAIL, binding.email.getText().toString());
                            preferenceManager.putString(Constants.KEY_PASSWORD, binding.password.getText().toString());
                            preferenceManager.putString(Constants.KEY_CONFROM, binding.confrimPassword.getText().toString());
                            preferenceManager.putString(Constants.KEY_PHONE, binding.phone.getText().toString());

                            binding.phone.setVisibility(View.GONE);
                            binding.phone.setEnabled(false);

                            Toast.makeText(SignUpActivity.this, "Your Account Create successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            dialog.dismiss();
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (phoneE.isEmpty()) {
                            binding.phone.setError("Phone Number is required");
                            binding.phone.requestFocus();
                            dialog.dismiss();
                            return;
                        }
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            String uid = task.getResult().getUser().getUid();
                            User userModel = new User(uid, Name, Email, Password, ConfirmPassword, phone, 1, 50);
                            database.getReference()
                                    .child("Partner")
                                    .child(uid)
                                    .setValue(userModel);

                            savePhoto(uid, "Partner", "partner_photo");

                            preferenceManager.putString(Constants.KEY_NAME, binding.Name.getText().toString());
                            preferenceManager.putString(Constants.KEY_EMAIL, binding.email.getText().toString());
                            preferenceManager.putString(Constants.KEY_PASSWORD, binding.password.getText().toString());
                            preferenceManager.putString(Constants.KEY_CONFROM, binding.confrimPassword.getText().toString());

                            binding.phone.setVisibility(View.VISIBLE);
                            binding.phone.setEnabled(true);
                            Toast.makeText(SignUpActivity.this, "Your Account Create successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            dialog.dismiss();
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 8) {
            if (data != null) {
                if (data.getData() != null) {

                    profileURI = data.getData();
                    binding.profileCiv.setImageURI(profileURI);

                    /*Uri uri = data.getData();
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
                    });*/

                }
            }
        }

    }

    private void savePhoto(String uid, String userType, String storageType){
        final StorageReference reference = storage.getReference()
                .child(storageType)
                .child(uid);
        //dialog.show();
        reference.putFile(profileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(@NonNull @NotNull UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getContext(), "Profile Saved", Toast.LENGTH_SHORT).show();
                //dialog.dismiss();
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Uri uri) {
                        database.getReference()
                                .child(userType)
                                .child(uid)
                                .child("Profile")
                                .setValue(uri.toString());


                    }
                });
            }
        });
    }
}