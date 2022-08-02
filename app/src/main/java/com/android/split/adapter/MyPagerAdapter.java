package com.android.split.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyPagerAdapter extends FragmentStateAdapter {

    private final Fragment[] fragments;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, Fragment[] fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return this.fragments[position];
    }

    @Override
    public int getItemCount() {
        return this.fragments.length;
    }

}
