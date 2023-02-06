package com.example.guided_app.fragment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guided_app.CommentActivity;
import com.example.guided_app.R;
import com.example.guided_app.databinding.NotificationsSampleBinding;
import com.example.guided_app.fragment.model.NotificationModel;
import com.example.guided_app.fragment.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    ArrayList<NotificationModel> list;
    Context context;

    public NotificationAdapter(ArrayList<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notifications_sample,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        NotificationModel model =list.get(position);

        String type = model.getType();

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getNotificationBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getProfilePhoto()).placeholder(R.drawable.placeholder).into(holder.binding.profileImage);

                if(type.equals("like")){
                    holder.binding.notificationtext.setText(Html.fromHtml("<b>"+user.getName()+"<b>"+" liked your post"));
                }
                else if(type.equals("comment")){
                    holder.binding.notificationtext.setText(Html.fromHtml("<b>"+user.getName()+"<b>"+" commented on your post"));
                }
                else if(type.equals("follow")){
                    holder.binding.notificationtext.setText(Html.fromHtml("<b>"+user.getName()+"<b>"+" Followed you"));
                }
                else{
                    holder.binding.notificationtext.setText(Html.fromHtml("<b>"+user.getName()+"<b>"+" Viewed your profile"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!type.equals("guided")) {

                }
                else if(!type.equals("request")){}

                else{
                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(model.getPostedBy()).child(model.getNotificationBy()).child("checkOpen").setValue(true);

                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", model.getPostId());
                    intent.putExtra("postedBy", model.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        Boolean checkOpen = model.isCheckOpen();
        if(checkOpen== true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else{
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        NotificationsSampleBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = NotificationsSampleBinding.bind(itemView);
        }
    }
}
