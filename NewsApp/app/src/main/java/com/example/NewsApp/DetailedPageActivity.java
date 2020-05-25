package com.example.NewsApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.NewsApp.ui.home.NewsListFragment;
import com.example.NewsApp.ui.home.News_Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class DetailedPageActivity extends AppCompatActivity {

    private String article_id;
    private RelativeLayout mProgressBar;
    private ImageButton mReturn_btn, mBookMarkBtn, mTwitterBtn;
    private RequestQueue mRequestQueue;
    private String mImageUrl, mTitle, mDate, mOriginalDate, mSection, mDescription, mArticleUrl;
    private TextView mBarTitle, mDetailedPageTitle, mDetailedPageSection, mDetailedPageDate, mDetailedPageDescription, mDetailedPageViewMoreBtn;
    private ImageView mDetailedPageImage;
    private CardView mDetailedPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_page);

        Intent intent = this.getIntent();
        article_id = intent.getStringExtra("id");

        mReturn_btn = findViewById(R.id.detailedPage_return_btn);
        mReturn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBarTitle = findViewById(R.id.detailedPage_bar_title);
        mBookMarkBtn = findViewById(R.id.detailedPage_bookmark_btn);
        mTwitterBtn = findViewById(R.id.detailedPage_twitter_btn);
        mProgressBar = findViewById(R.id.detailedPage_ProgressBarContainer);
        mDetailedPage = findViewById(R.id.detailedPage);
        mDetailedPageImage = findViewById(R.id.detailedPage_image);
        mDetailedPageTitle = findViewById(R.id.detailedPage_title);
        mDetailedPageSection = findViewById(R.id.detailedPage_section);
        mDetailedPageDate = findViewById(R.id.detailedPage_date);
        mDetailedPageDescription = findViewById(R.id.detailedPage_description);
        mDetailedPageViewMoreBtn = findViewById(R.id.detailedPage_viewMoreBtn);
        mDetailedPageViewMoreBtn.setPaintFlags(mDetailedPageViewMoreBtn.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
        mDetailedPageViewMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mArticleUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://jh-csci571-hw9-backend.azurewebsites.net/api/guaridan/article/" + article_id.replaceAll("/", "_");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONSharedPreferences mJSONSharedPreferences = new JSONSharedPreferences();
                    JSONObject jsonObj = new JSONObject(response);
                    mArticleUrl = jsonObj.getString("url");
                    mImageUrl = jsonObj.getString("image");
                    mTitle = jsonObj.getString("title");
                    mDate = jsonObj.getString("time");
                    mOriginalDate = mDate;
                    mSection = jsonObj.getString("section");
                    mDescription = jsonObj.getString("description");

                    mBarTitle.setText(mTitle);
                    mDetailedPageTitle.setText(mTitle);
                    mDetailedPageSection.setText(mSection);
                    ZonedDateTime zonedDateTimeInUTC = ZonedDateTime.parse(mDate.substring(0, 19)+"+00:00");
                    ZonedDateTime zonedDateTimeInPST = zonedDateTimeInUTC.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
                    mDate = mDate.substring(5, 7) + " " + zonedDateTimeInPST.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " " + zonedDateTimeInPST.getYear();
                    mDetailedPageDate.setText(mDate);
                    mDetailedPageDescription.setText(HtmlCompat.fromHtml(mDescription, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    Glide.with(getBaseContext())
                            .load(mImageUrl)
                            .into(mDetailedPageImage);

                    mProgressBar.setVisibility(View.GONE);

                    mBookMarkBtn.setVisibility(View.VISIBLE);
                    if (mJSONSharedPreferences.isExisted(getBaseContext(), article_id)) {
                        mBookMarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                    }

                    mTwitterBtn.setVisibility(View.VISIBLE);
                    mDetailedPage.setVisibility(View.VISIBLE);

                    mBookMarkBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (mJSONSharedPreferences.isExisted(getBaseContext(), article_id)) {
                                    mBookMarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange);
                                    mJSONSharedPreferences.removeNews(getBaseContext(), article_id);
                                    Toast.makeText(getBaseContext(), mTitle + " was removed from favorites", Toast.LENGTH_SHORT).show();

                                }
                                else {
                                    mBookMarkBtn.setBackgroundResource(R.drawable.ic_bookmark_orange_filled);
                                    mJSONSharedPreferences.addNews(getBaseContext(), mTitle, mOriginalDate, mSection, article_id, mImageUrl);
                                    Toast.makeText(getBaseContext(), mTitle + " was added to bookmarks", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    mTwitterBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "http://www.twitter.com/intent/tweet?url=" + mArticleUrl + "&text=Check out this link:" + "&hashtags=CSCI571NewsSearch";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", error.toString());
            }
        });

        mRequestQueue.add(stringRequest);
    }

}

