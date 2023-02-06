package com.example.guided_app;

import static com.example.guided_app.R.drawable.following_btn;
import static com.example.guided_app.R.drawable.placeholder;
import static com.example.guided_app.R.drawable.rqt_btn_color;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.guided_app.databinding.ActivityVactivityBinding;
import com.example.guided_app.fragment.Adapter.guidedAdapter;
import com.example.guided_app.fragment.model.NotificationModel;
import com.example.guided_app.fragment.model.Req_model;
import com.example.guided_app.fragment.model.User;
import com.example.guided_app.fragment.model.guidedModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class VActivity extends AppCompatActivity {

    ArrayList<guidedModel> list;
    ActivityVactivityBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database, userRef;
    String userid;
    Dialog dialog;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityVactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database= getInstance();

        Intent intent = getIntent();
        userid= intent.getStringExtra("uid");
        userRef = FirebaseDatabase.getInstance();
        DatabaseReference reff= userRef.getReference(userid);


        //request button
       reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.reqBtn.setBackgroundResource(following_btn);
                    binding.reqBtn.setText("Following");
                    binding.reqBtn.setTextColor(rqt_btn_color);
                    binding.reqBtn.setEnabled(false);
                }
                else{
                    binding.reqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Req_model requested = new Req_model();
                            requested.setRequestedBy(FirebaseAuth.getInstance().getUid());
                            requested.setRequestedTo(userid);
                            requested.setRequestedAt(new Date().getTime());
                            binding.reqBtn.setBackgroundResource(following_btn);
                            binding.reqBtn.setText("Requested");


                            FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("requests").child(FirebaseAuth.getInstance().getUid()).setValue(requested).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(VActivity.this, "Request send", Toast.LENGTH_SHORT).show();
                                    //request notification
                                    NotificationModel notification = new NotificationModel();
                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                    notification.setNotificationAt(new Date().getTime());
                                    notification.setType("request");

                                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid).push().setValue(notification);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       //get username
        database.getReference().child("Users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user= snapshot.getValue(User.class);
                    Picasso.get().load(user.getCoverPhoto()).placeholder(placeholder).into(binding.imageView6);
                    Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(binding.profileImage);
                    binding.username.setText(user.getName());
                    binding.profession.setText(user.getProfession());
                    binding.textView9.setText(user.getGuidedCount()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        list = new ArrayList<>();


        //setting user guided on rv
        guidedAdapter adapter =new guidedAdapter(list, this);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.guideRv.setLayoutManager(linearLayoutManager);
        binding.guideRv.setAdapter(adapter);

        database.getReference().child("Users").child(userid).child("guided").addValueEventListener(new ValueEventListener() {
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








//setting of pay Option
        uri= Uri.parse("https://pay.google.com/") ;
                binding.paymentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                    }
                });
               dialog= new Dialog(VActivity.this);
               dialog.setContentView(R.layout.pay_alert);
               dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        Button pay = dialog.findViewById(R.id.payBtn);
        Button nopay = dialog.findViewById(R.id.nopayBtn);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent payintent= new Intent(Intent.ACTION_VIEW);
                payintent.setData(uri);
                Intent chooser = Intent.createChooser(payintent,"Pay With");
                startActivity(payintent);
            }
        });

        nopay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
// guidedModel guided = new guidedModel();
//                            guided.setGuidedBy(FirebaseAuth.getInstance().getUid());
//                            guided.setGuidedAt(new Date().getTime());
//
//                            FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("guided").child(FirebaseAuth.getInstance().getUid()).setValue(guided).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("guidedCount").setValue(userid).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            binding.reqBtn.setBackgroundColor(R.drawable.following_btn);
//                                            binding.reqBtn.setText("Requested");
//                                            binding.reqBtn.setTextColor(R.drawable.rqt_btn_color);
//                                            binding.reqBtn.setEnabled(false);
//                                            //Toast.makeText(this, "You Request guidance"+ userid, Toast.LENGTH_SHORT).show();
//
//                                            NotificationModel notification = new NotificationModel();
//                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
//                                            notification.setNotificationAt(new Date().getTime());
//                                            notification.setType("follow");
//
//                                            FirebaseDatabase.getInstance().getReference().child("Notifications").child(userid).push().setValue(notification);
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                    });