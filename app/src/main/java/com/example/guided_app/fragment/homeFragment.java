package com.example.guided_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guided_app.R;
import com.example.guided_app.fragment.Adapter.Post_Adapter;
import com.example.guided_app.fragment.model.Post_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class homeFragment extends Fragment {

    RecyclerView dasboardRV;
    ArrayList<Post_model> postList;
    FirebaseAuth auth;
    FirebaseDatabase database;


    public homeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =inflater.inflate(R.layout.fragment_home, container, false);


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        dasboardRV = view.findViewById(R.id.dashboardrv);
        postList = new ArrayList<>();



        Post_Adapter postAdapterAdapter = new Post_Adapter(postList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dasboardRV.setLayoutManager(layoutManager);
        dasboardRV.addItemDecoration(new DividerItemDecoration(dasboardRV.getContext(), DividerItemDecoration.VERTICAL));
        dasboardRV.setNestedScrollingEnabled(false);
        dasboardRV.setAdapter(postAdapterAdapter);


        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                   // postList.clear();
                    Post_model post = dataSnapshot.getValue(Post_model.class);
                    post.setPostId(dataSnapshot.getKey());
                    postList.add(post);
                }
                postAdapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // Inflate the layout for this fragment
        return view;
    }
}