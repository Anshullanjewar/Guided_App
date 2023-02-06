package com.example.guided_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.guided_app.R;
import com.example.guided_app.fragment.Adapter.viewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class notificationFragment extends Fragment {
    //ViewPager2 viewPager2;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    viewPagerAdapter vpa;
    private String[] titles = new String[]{"Notification", "Request"};

    public notificationFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        viewPager2 = view.findViewById(R.id.viewPager);
        tabLayout=view.findViewById(R.id.tabLayout);
        vpa = (new viewPagerAdapter(this));
        viewPager2.setAdapter(vpa);
       // tabLayout.setupWithViewPager(viewpager);

         new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> tab.setText(titles[position])).attach();
        return view;
    }
}