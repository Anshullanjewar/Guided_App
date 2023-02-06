package com.example.guided_app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.guided_app.R;
import com.example.guided_app.databinding.FragmentProfileBinding;
import com.example.guided_app.fragment.Adapter.guidedAdapter;
import com.example.guided_app.fragment.model.User;
import com.example.guided_app.fragment.model.guidedModel;
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

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class profileFragment extends Fragment {


   // RecyclerView recyclerView;
    ArrayList<guidedModel>list;
    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
   // ActivityResultLauncher<String> mtakePhoto;
    Uri imageUri;
    ActivityResultLauncher cphoto;

    public profileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database= FirebaseDatabase.getInstance();
//        mtakePhoto = registerForActivityResult(
//                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//                        binding.imageView6.setImageURI(result);
//                        imageUri =result;
//                    }
//                }
//        );
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentProfileBinding.inflate(inflater,container,false);
        //View view =inflater.inflate(R.layout.fragment_profile,container,false);
      //  recyclerView = view.findViewById(R.id.guideRv);

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user= snapshot.getValue(User.class);
                    Picasso.get().load(user.getCoverPhoto()).placeholder(R.drawable.placeholder).into(binding.imageView6);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(binding.profileImage);
                    binding.username.setText(user.getName());
                    binding.profession.setText(user.getProfession());
                    binding.textView9.setText(user.getGuidedCount()+"");
                    binding.about.setText(user.getAbout());
                    binding.textView10.setText(user.getPrice()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



       list = new ArrayList<>();


        guidedAdapter adapter =new guidedAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        binding.guideRv.setLayoutManager(linearLayoutManager);
        binding.guideRv.setAdapter(adapter);

        database.getReference().child("Users").child(auth.getUid()).child("guided").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    guidedModel guided = dataSnapshot.getValue(guidedModel.class);
                    list.add(guided);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.coverchange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ImagePicker.Builder with = ImagePicker.with(profileFragment.this);
                with.crop(16f, 9f);
                with.createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        cphoto.launch(intent );
                        return null;
                    }
                });
                //uploadImage();
//                Intent myGallery = new Intent(Intent.ACTION_PICK);
//                myGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                postImg.launch(myGallery);
            }
        });

        return binding.getRoot();
    }



    private void uploadImage() {
        if (imageUri != null) {
            StorageReference reference = storage.getReference().child("cover_photo").child(FirebaseAuth.getInstance().getUid());
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "cover photo", Toast.LENGTH_SHORT).show();
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

}