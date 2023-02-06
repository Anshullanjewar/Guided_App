package com.example.guided_app.fragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guided_app.R;
import com.example.guided_app.databinding.RequestSampleBinding;
import com.example.guided_app.fragment.model.NotificationModel;
import com.example.guided_app.fragment.model.Req_model;
import com.example.guided_app.fragment.model.User;
import com.example.guided_app.fragment.model.guidedModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class Req_Adapter extends RecyclerView.Adapter<Req_Adapter.viewHolder>{

    ArrayList<Req_model> list;
    Context context;
    String currentuser;
    String auser;

    public Req_Adapter(ArrayList<Req_model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_sample,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Req_model model = list.get(position);
        currentuser= FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getRequestedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfile()).placeholder(R.drawable.placeholder).into(holder.binding.profileImage);
                holder.binding.name.setText(user.getName());
                holder.binding.profession.setText(user.getProfession());
                auser = model.getRequestedBy();



                holder.binding.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        guidedModel guided = new guidedModel();
                        guided.setGuidedBy(model.getRequestedBy());
                        guided.setGuidedAt(new Date().getTime());
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("guided").child(model.getRequestedBy()).setValue(guided).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("guidedCount").setValue(user.getGuidedCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Accepeted", Toast.LENGTH_SHORT).show();

                                        NotificationModel notification = new NotificationModel();
                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notification.setNotificationAt(new Date().getTime());
                                        notification.setType("follow");

                                        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getUid()).push().setValue(notification);
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        RequestSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RequestSampleBinding.bind(itemView);
        }
    }
}
