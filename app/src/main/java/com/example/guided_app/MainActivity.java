package com.example.guided_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.guided_app.databinding.ActivityMainBinding;
import com.example.guided_app.fragment.addpostFragment;
import com.example.guided_app.fragment.exploreFragment;
import com.example.guided_app.fragment.homeFragment;
import com.example.guided_app.fragment.notificationFragment;
import com.example.guided_app.fragment.profileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
FirebaseAuth auth = FirebaseAuth.getInstance();
DrawerLayout drawerLayout;
    FirebaseStorage storage;
    FirebaseDatabase database =  FirebaseDatabase.getInstance();
NavigationView navigationView;
    ActivityResultLauncher<String> coverPhotochange;
    Uri imageUri;
    ImageView cover_picView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("");

        storage = FirebaseStorage.getInstance();
        cover_picView = findViewById(R.id.imageView6);
//        coverPhotochange = registerForActivityResult(
//                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//                        cover_picView.setImageURI(result);
//                        imageUri =result;
//                    }
//                }
//        );

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, binding.toolbar,R.string.navigation_open,R.string.navigation_close );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ViewCompat.setLayoutDirection(binding.toolbar,ViewCompat.LAYOUT_DIRECTION_RTL);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
                else{
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });


        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        navigationView.setVisibility(View.GONE);
        drawerLayout.setDrawerLockMode(drawerLayout.LOCK_MODE_LOCKED_CLOSED);
        transaction.replace(R.id.container,new homeFragment());
        transaction.commit();

        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
                switch(i){
                    case 0:
                        binding.toolbar.setVisibility(View.GONE);
                        navigationView.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new homeFragment());
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                        case 1:
                            binding.toolbar.setVisibility(View.GONE);
                            navigationView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Explore", Toast.LENGTH_SHORT).show();
                            transaction.replace(R.id.container,new exploreFragment());
                        break;
                        case 2:
                            binding.toolbar.setVisibility(View.GONE);
                            navigationView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Post", Toast.LENGTH_SHORT).show();
                            transaction.replace(R.id.container,new addpostFragment());
                        break;
                        case 3:
                            binding.toolbar.setVisibility(View.GONE);
                            navigationView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Notification", Toast.LENGTH_SHORT).show();
                            transaction.replace(R.id.container,new notificationFragment());
                        break;
                        case 4:
                            binding.toolbar.setVisibility(View.VISIBLE);
                            navigationView.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                            transaction.replace(R.id.container,new profileFragment());
                        break;
                }
                transaction.commit();
            }
        });

        Intent intent1= new Intent(MainActivity.this,EditActivity.class);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.change_name:
                        Toast.makeText(getApplicationContext(),"Name",Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.change_bio:
                        Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                        break;
                    case R.id.change_price:
                        Toast.makeText(getApplicationContext(),"Change Price",Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                        break;
                    case R.id.profile_pic:
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        startActivity(intent1);
                        Toast.makeText(MainActivity.this,"Change Profile Image",Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.cover_pic:
                        Toast.makeText(MainActivity.this,"Change Cover Image",Toast.LENGTH_SHORT).show();
                        startActivity(intent1);
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        break;
                    case R.id.logOut:
                        Toast.makeText(MainActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                }

                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode== Activity.RESULT_OK){
            imageUri = data.getData();
            cover_picView.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference reference = storage.getReference().child("cover_photo").child(FirebaseAuth.getInstance().getUid());
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Toast.makeText(MainActivity.this,"cover Photo Changed",Toast.LENGTH_SHORT).show();
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