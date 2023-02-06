package com.example.guided_app.fragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guided_app.R;
import com.example.guided_app.databinding.CommentSampleBinding;
import com.example.guided_app.fragment.model.Comment;
import com.example.guided_app.fragment.model.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {


    Context context;
    ArrayList<Comment> list;

    public CommentAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_sample,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Comment comment = list.get(position);
       holder.binding.postDescription.setText(comment.getCommentBody());
       holder.binding.username.setText(comment.getCommentedBy());
        String time = TimeAgo.using(comment.getCommentedAt());
        holder.binding.time.setText(time);
//
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(holder.binding.profileImage);
                holder.binding.username.setText(user.getName());
                holder.binding.postDescription.setText(comment.getCommentBody());

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


        CommentSampleBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CommentSampleBinding.bind(itemView);
        }
    }
}
