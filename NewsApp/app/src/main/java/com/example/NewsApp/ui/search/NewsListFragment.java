package com.example.NewsApp.ui.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.NewsApp.DetailedPageActivity;
import com.example.NewsApp.JSONSharedPreferences;
import com.example.NewsApp.MainActivity;
import com.example.NewsApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class NewsListFragment extends Fragment
{
    private final String TAG = "NewsListFragment";
    private RelativeLayout mProgressBar;
    private RecyclerView mRecyclerView;
    private ListAdapter mListadapter;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String url = "https://jh-csci571-hw9-backend.azurewebsites.net/api/guaridan/news/search/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        SearchActivity activity = (SearchActivity) getActivity();
        url = url + activity.getKeyword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mProgressBar = view.findViewById(R.id.homeProgressBarContainer);
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Task().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        new Task().execute();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            final ArrayList newsItems = new ArrayList<News_Item>();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jArray = new JSONArray(response);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            newsItems.add(
                                    new News_Item
                                            (
                                                    oneObject.getString("image"),
                                                    oneObject.getString("title"),
                                                    oneObject.getString("time"),
                                                    oneObject.getString("section"),
                                                    oneObject.getString("id")
                                            ));
                        }
                        mListadapter = new ListAdapter(newsItems);
                        mRecyclerView.setAdapter(mListadapter);
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            });

            mRequestQueue.add(stringRequest);

            return null;
        }
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
    {
        private ArrayList<News_Item> newsList;

        public ListAdapter(ArrayList<News_Item> newsItems)
        {
            this.newsList = newsItems;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView textViewTitle;
            TextView textViewTime;
            TextView textViewSection;
            String newsId;
            ImageView cardImage;
            ImageButton cardBookmarkImage ;

            public ViewHolder(View itemView) {
                super(itemView);
                this.textViewTitle =  itemView.findViewById(R.id.cardTitle);
                this.textViewTime =  itemView.findViewById(R.id.cardTime);
                this.textViewSection =  itemView.findViewById(R.id.cardSection);
                this.cardImage = itemView.findViewById(R.id.cardImage);
                this.cardBookmarkImage = itemView.findViewById(R.id.cardBookmark);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news_item, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position)
        {
            JSONSharedPreferences mJSONSharedPreferences = new JSONSharedPreferences();
            holder.textViewTitle.setText(newsList.get(position).getTitle());
            holder.textViewTime.setText(newsList.get(position).getTime());
            holder.textViewSection.setText(newsList.get(position).getSection());
            holder.newsId = newsList.get(position).getId();
            Glide.with(getActivity())
                    .load(newsList.get(position).getImageUrl())
                    .into(holder.cardImage);

            try {
                if (mJSONSharedPreferences.isExisted(getContext(), newsList.get(position).getId())) {
                    holder.cardBookmarkImage.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.cardBookmarkImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mJSONSharedPreferences.isExisted(getContext(), newsList.get(position).getId())) {
                            holder.cardBookmarkImage.setBackgroundResource(R.drawable.ic_bookmark_orange);
                            mJSONSharedPreferences.removeNews(getContext(), newsList.get(position).getId());
                            Toast.makeText(getContext(), newsList.get(position).getTitle() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            holder.cardBookmarkImage.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                            mJSONSharedPreferences.addNews(getContext(), newsList.get(position).getTitle(),
                                    newsList.get(position).getOriginalTime(),
                                    newsList.get(position).getSection(),
                                    newsList.get(position).getId(),
                                    newsList.get(position).getImageUrl());
                            Toast.makeText(getContext(), newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Item " + position + " is shortly clicked.", Toast.LENGTH_SHORT).show();
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
                            .load(newsList.get(position).getImageUrl())
                            .into(dialogImage);

                    TextView dialogTitle = dialog.findViewById(R.id.dialog_newsCardTitle);
                    dialogTitle.setText(newsList.get(position).getTitle());

                    ImageButton dialogTwitterBtn = dialog.findViewById(R.id.dialog_newsCardTwitterIcon);
                    dialogTwitterBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "http://www.twitter.com/intent/tweet?url=" + "https://theguardian.com/" + newsList.get(position).getId() + "&text=Check out this link:" + "&hashtags=CSCI571NewsSearch";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });

                    ImageButton dialogBookmarkBtn = dialog.findViewById(R.id.dialog_newsCardBookMarkIcon);

                    try {
                        if (mJSONSharedPreferences.isExisted(getContext(), newsList.get(position).getId())) {
                            dialogBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialogBookmarkBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mJSONSharedPreferences.isExisted(getContext(), newsList.get(position).getId())) {
                                    holder.cardBookmarkImage.setBackgroundResource(R.drawable.ic_bookmark_orange);
                                    dialogBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange);
                                    mJSONSharedPreferences.removeNews(getContext(), newsList.get(position).getId());
                                    Toast.makeText(getContext(), newsList.get(position).getTitle() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    holder.cardBookmarkImage.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                                    dialogBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                                    mJSONSharedPreferences.addNews(getContext(), newsList.get(position).getTitle(),
                                            newsList.get(position).getOriginalTime(),
                                            newsList.get(position).getSection(),
                                            newsList.get(position).getId(),
                                            newsList.get(position).getImageUrl());
                                    Toast.makeText(getContext(), newsList.get(position).getTitle() + " was added to bookmarks", Toast.LENGTH_SHORT).show();
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
            return newsList.size();
        }
    }

    @Override
    public void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }
}