package com.example.guided_app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.guided_app.R;
import com.example.guided_app.databinding.FragmentAddpostBinding;
import com.example.guided_app.fragment.model.Post_model;
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

import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class addpostFragment extends Fragment {

    FragmentAddpostBinding binding;
    //ActivityResultLauncher<String> postImg;
    Uri imag;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    ActivityResultLauncher postImg;

    public addpostFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());


//        postImg=registerForActivityResult(
//                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//                        binding.postAddImage.setImageURI(result);
//                        imgUri =result;
//                    }
//                }
//        );
//        private String startForProfileImageResult =
//                registerForActivityResult(new ActivityResultContracts.StartActivityForResult()) { result:ActivityResult ->
//                int resultCode = result.resultCode;
//            int data = result.data;
//
//            if (resultCode == Activity.RESULT_OK) {
//                //Image Uri will not be null for RESULT_OK
//                val fileUri = data?.data!!
//
//                        mProfileUri = fileUri
//                imgProfile.setImageURI(fileUri)
//            } else if (resultCode == ImagePicker.RESULT_ERROR) {
//                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
//            }
//        }
         postImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                         imag = result.getData().getData();
                        if (imag != null) {
                            binding.postAddImage.setImageURI(imag);
                        }
                    }
                });


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddpostBinding.inflate(inflater, container, false);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Posting...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(binding.profileImage);
                    binding.name.setText(user.getName());
                    binding.profession.setText(user.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.txxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description = binding.txxt.getText().toString();
                if(!description.isEmpty()){
                    binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn));
                    binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postButton.setEnabled(true);
                }
                else{
                    binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.following_btn));
                    binding.postButton.setTextColor(getContext().getResources().getColor(android.R.color.system_neutral2_500));
                    binding.postButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ImagePicker.Builder with = ImagePicker.with(addpostFragment.this);
                with.crop();
                with.compress(1024);
                with.maxResultSize(1080, 1080);
                with.createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        postImg.launch(intent );
                        return null;
                    }
                });


                binding.postAddImage.setVisibility(View.VISIBLE);
                binding.postButton.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn));
                binding.postButton.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.postButton.setEnabled(true);

            }
        });





        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();

                final StorageReference reference= storage.getReference().child("posts").child(FirebaseAuth.getInstance().getUid()).child(new Date().getTime()+"");
                reference.putFile(imag).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               Post_model post = new Post_model();
                               post.setPostImage(uri.toString());
                               post.setPostedBy(FirebaseAuth.getInstance().getUid());
                               post.setPostDescription(binding.txxt.getText().toString());
                               post.setPostedAt(new Date().getTime());


                               database.getReference().child("posts").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {
                                       dialog.dismiss();
                                       Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                   }
                               });
                           }
                       });
                    }
                });
            }
        });


        return binding.getRoot();
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==110 && resultCode== Activity.RESULT_OK){
//            imgUri = data.getData();
//            binding.postAddImage.setImageURI(imgUri);
//        }
//    }

}