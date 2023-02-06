package com.example.guided_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guided_app.databinding.ActivityEditBinding;
import com.example.guided_app.fragment.model.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri imageUri;
    Uri profileUri;
    ActivityResultLauncher cphoto,pphoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database= FirebaseDatabase.getInstance();

        cphoto= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();
                        if (imageUri != null) {
                            binding.imageView6.setImageURI(imageUri);
                            uploadImage();
                        }
                    }
                });
        pphoto= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        profileUri = result.getData().getData();
                        if (profileUri != null) {
                            binding.profileImage.setImageURI(profileUri);
                            uploadProfileImage();
                        }
                    }
                });

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user= snapshot.getValue(User.class);
                    Picasso.get().load(user.getCoverPhoto()).placeholder(R.drawable.placeholder).into(binding.imageView6);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(binding.profileImage);
                    binding.editNameText.setText(user.getName());
                    binding.editAboutText.setText(user.getAbout());
                    binding.editPriceText.setText(user.getPrice()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.editNameText!= null){
                    database.getReference().child("Users").child(auth.getUid()).child("name").setValue(binding.editNameText.getText().toString());
                }
                if(binding.editAboutText!=null){
                    database.getReference().child("Users").child(auth.getUid()).child("About").setValue(binding.editAboutText.getText().toString());
                }
                if(binding.editPriceText!=null){
                    database.getReference().child("Users").child(auth.getUid()).child("price").setValue(binding.editPriceText.getText()+"");
                }
                Toast.makeText(EditActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        binding.imageView6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ImagePicker.Builder with = ImagePicker.with(EditActivity.this);
                with.crop(16f, 9f);
                with.createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        cphoto.launch(intent );
                        return null;
                    }
                });
            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ImagePicker.Builder with = ImagePicker.with(EditActivity.this);
                with.crop(16f, 9f);
                with.createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        pphoto.launch(intent );
                        return null;
                    }
                });
            }
        });

        binding.editDoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2= new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent2);
            }
        });
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference reference = storage.getReference().child("cover_photo").child(FirebaseAuth.getInstance().getUid());
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditActivity.this, "cover photo", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(auth.getUid()).child("coverPhoto").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }
    private void uploadProfileImage() {
        if (profileUri != null) {
            StorageReference reference = storage.getReference().child("profile_photo").child(FirebaseAuth.getInstance().getUid());
            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditActivity.this, "profile photo", Toast.LENGTH_SHORT).show();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(auth.getUid()).child("ProfilePhoto").setValue(uri.toString());
                        }
                    });
                }
            });
        }
    }
}