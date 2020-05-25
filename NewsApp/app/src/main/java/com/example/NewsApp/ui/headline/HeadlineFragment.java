package com.example.NewsApp.ui.headline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.NewsApp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HeadlineFragment extends Fragment {
    private HeadlineCollectionAdapter headlineCollectionAdapter;
    private ViewPager2 viewPager;
    private final String[] tabTitle = {"WORLD", "BUSINESS", "POLITICS", "SPORTS", "TECHNOLOGY", "SCIENCE"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        headlineCollectionAdapter = new HeadlineCollectionAdapter(this);
        viewPager = view.findViewById(R.id.headlineViewPager);
        viewPager.setAdapter(headlineCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitle[position])
        ).attach();
    }
}