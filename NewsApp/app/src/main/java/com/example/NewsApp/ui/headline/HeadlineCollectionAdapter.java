package com.example.NewsApp.ui.headline;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HeadlineCollectionAdapter extends FragmentStateAdapter {
    public HeadlineCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(NewsListFragment.ARG_OBJECT, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}