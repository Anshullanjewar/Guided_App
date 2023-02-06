package com.example.guided_app.fragment.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.guided_app.fragment.notificationsFragment;
import com.example.guided_app.fragment.requestFragment;

public class viewPagerAdapter extends FragmentStateAdapter {
    private String[] titles = new String[]{"Notification", "Request"};

    public viewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new notificationsFragment();
            case 1: return new requestFragment();
        }
        return new notificationsFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
