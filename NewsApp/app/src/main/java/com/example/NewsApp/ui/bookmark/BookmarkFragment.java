package com.example.NewsApp.ui.bookmark;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.NewsApp.JSONSharedPreferences;
import com.example.NewsApp.R;

import org.json.JSONException;

public class BookmarkFragment extends Fragment {
    private View mBookmarkFragment;
    private TextView mBookmarkText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bookmark, container, false);

        mBookmarkFragment = root.findViewById(R.id.bookmarkList);
        mBookmarkText = root.findViewById(R.id.bookmarkText);
        JSONSharedPreferences mJSONSharedPreferences = new JSONSharedPreferences();
        try {
            if (mJSONSharedPreferences.isEmpty(getContext())) {
                mBookmarkFragment.setVisibility(View.GONE);
                mBookmarkText.setVisibility(View.VISIBLE);
            } else {
                mBookmarkFragment.setVisibility(View.VISIBLE);
                mBookmarkText.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}