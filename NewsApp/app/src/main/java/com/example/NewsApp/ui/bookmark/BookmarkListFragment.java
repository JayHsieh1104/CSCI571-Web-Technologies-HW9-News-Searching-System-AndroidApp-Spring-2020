package com.example.NewsApp.ui.bookmark;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.NewsApp.DetailedPageActivity;
import com.example.NewsApp.JSONSharedPreferences;
import com.example.NewsApp.R;
import com.example.NewsApp.ui.home.News_Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class BookmarkListFragment extends Fragment
{
    private final String TAG = "BookmarkListFragment";
    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_bookmark_list, container, false);

        mRecyclerView = view.findViewById(R.id.bookmarkRecyclerView);

        final ArrayList bookmarkItems = new ArrayList<Bookmark_Item>();
        JSONSharedPreferences mJSONSharedPreferences = new JSONSharedPreferences();

        try {
            if (!mJSONSharedPreferences.isEmpty(getContext())) {
                JSONArray jArray = mJSONSharedPreferences.loadJSONArray(getContext());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    bookmarkItems.add(
                            new Bookmark_Item
                                    (
                                            oneObject.getString("newsImageUrl"),
                                            oneObject.getString("newsTitle"),
                                            oneObject.getString("newsTime"),
                                            oneObject.getString("newsSection"),
                                            oneObject.getString("newsID")
                                    ));
                }
                mListadapter = new ListAdapter(bookmarkItems);
                mRecyclerView.setAdapter(mListadapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
    {
        private ArrayList<Bookmark_Item> bookmarksList;

        public ListAdapter(ArrayList<Bookmark_Item> bookmarkItems)
        {
            this.bookmarksList = bookmarkItems;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView textViewTitle;
            TextView textViewTime;
            TextView textViewSection;
            String newsId;
            ImageView cardImage;
            ImageButton cardBookmarkBtn ;

            public ViewHolder(View itemView) {
                super(itemView);
                this.textViewTitle =  itemView.findViewById(R.id.bookmarkTitle);
                this.textViewTime =  itemView.findViewById(R.id.bookmarkTime);
                this.textViewSection =  itemView.findViewById(R.id.bookmarkSection);
                this.cardImage = itemView.findViewById(R.id.bookmarkImage);
                this.cardBookmarkBtn = itemView.findViewById(R.id.bookmarkBookmarkBtn);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bookmark_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position)
        {
            JSONSharedPreferences mJSONSharedPreferences = new JSONSharedPreferences();
            holder.textViewTitle.setText(bookmarksList.get(position).getTitle());
            holder.textViewTime.setText(bookmarksList.get(position).getTime());
            holder.textViewSection.setText(bookmarksList.get(position).getSection());
            holder.newsId = bookmarksList.get(position).getId();
            Glide.with(getActivity())
                    .load(bookmarksList.get(position).getImageUrl())
                    .into(holder.cardImage);

            try {
                if (mJSONSharedPreferences.isExisted(getContext(), bookmarksList.get(position).getId())) {
                    holder.cardBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.cardBookmarkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mJSONSharedPreferences.isExisted(getContext(), bookmarksList.get(position).getId())) {
                            holder.cardBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange);
                            mJSONSharedPreferences.removeNews(getContext(), bookmarksList.get(position).getId());
                            Toast.makeText(getContext(), bookmarksList.get(position).getTitle() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            holder.cardBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                            mJSONSharedPreferences.addNews(getContext(), bookmarksList.get(position).getTitle(),
                                    bookmarksList.get(position).getTime(),
                                    bookmarksList.get(position).getSection(),
                                    bookmarksList.get(position).getId(),
                                    bookmarksList.get(position).getImageUrl());
                            Toast.makeText(getContext(), bookmarksList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("id", holder.newsId);
                    intent.setClass(Objects.requireNonNull(getContext()), DetailedPageActivity.class);
                    startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // custom dialog
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_newscard);

                    // set the custom dialog components - text, image
                    ImageView dialogImage =  dialog.findViewById(R.id.dialog_newsCardImage);
                    Glide.with(getActivity())
                            .load(bookmarksList.get(position).getImageUrl())
                            .into(dialogImage);

                    TextView dialogTitle = dialog.findViewById(R.id.dialog_newsCardTitle);
                    dialogTitle.setText(bookmarksList.get(position).getTitle());

                    ImageButton dialogTwitterBtn = dialog.findViewById(R.id.dialog_newsCardTwitterIcon);
                    dialogTwitterBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "http://www.twitter.com/intent/tweet?url=" + "https://theguardian.com/" + bookmarksList.get(position).getId() + "&text=Check out this link:" + "&hashtags=CSCI571NewsSearch";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });

                    ImageButton dialogBookmarkBtn = dialog.findViewById(R.id.dialog_newsCardBookMarkIcon);

                    try {
                        if (mJSONSharedPreferences.isExisted(getContext(), bookmarksList.get(position).getId())) {
                            dialogBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialogBookmarkBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mJSONSharedPreferences.isExisted(getContext(), bookmarksList.get(position).getId())) {
                                    holder.cardBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange);
                                    dialogBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange);
                                    mJSONSharedPreferences.removeNews(getContext(), bookmarksList.get(position).getId());
                                    Toast.makeText(getContext(), bookmarksList.get(position).getTitle() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    holder.cardBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                                    dialogBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                                    mJSONSharedPreferences.addNews(getContext(), bookmarksList.get(position).getTitle(),
                                            bookmarksList.get(position).getTime(),
                                            bookmarksList.get(position).getSection(),
                                            bookmarksList.get(position).getId(),
                                            bookmarksList.get(position).getImageUrl());
                                    Toast.makeText(getContext(), bookmarksList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return bookmarksList.size();
        }
    }
}