package com.example.guided_app.fragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guided_app.R;
import com.example.guided_app.databinding.GuidedRvSampleBinding;
import com.example.guided_app.fragment.model.User;
import com.example.guided_app.fragment.model.guidedModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class guidedAdapter extends RecyclerView.Adapter<guidedAdapter.viewHolder> {

    ArrayList<guidedModel> list;
    Context context;

    public guidedAdapter(ArrayList<guidedModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.guided_rv_sample,parent,false);
        return new viewHolder(view);
     }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        guidedModel guided = list.get(position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(guided.getGuidedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(holder.binding.profileImage);
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

    public class viewHolder extends RecyclerView.ViewHolder {

        GuidedRvSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = GuidedRvSampleBinding.bind(itemView);
        }
    }
}
