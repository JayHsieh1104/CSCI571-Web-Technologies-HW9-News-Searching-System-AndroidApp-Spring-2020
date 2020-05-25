package com.example.NewsApp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.NewsApp.R;
import com.example.NewsApp.ui.home.HomeViewModel;

public class SearchActivity extends AppCompatActivity {

    private String keyword;
    private ImageButton mReturn_btn;
    private TextView mSearchTitle;
    public String getKeyword(){
        return keyword;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        keyword = intent.getStringExtra("keyword");

        setContentView(R.layout.activity_search_page);

        mSearchTitle = findViewById(R.id.search_bar_title);
        mSearchTitle.setText("Search Results for " + keyword);

        mReturn_btn = findViewById(R.id.search_return_btn);
        mReturn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

