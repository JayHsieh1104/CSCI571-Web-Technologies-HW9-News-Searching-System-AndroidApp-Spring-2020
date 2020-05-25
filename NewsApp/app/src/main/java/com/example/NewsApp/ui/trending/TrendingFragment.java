package com.example.NewsApp.ui.trending;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.NewsApp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private String mKeyWord = "Coronavirus";
    private final String mUrl = "https://jh-csci571-hw9-backend.azurewebsites.net/api/trending/search/";
    private LineChart mChart;
    private EditText mSearchEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trending, container, false);

        mSearchEditText = root.findViewById(R.id.trendingSearchEditText);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mKeyWord = v.getText().toString();
                    stringRequest();
                }
                return true;
            }
        });

        mChart = root.findViewById(R.id.trendingLineChart);
        mChart.getLegend().setTextSize(18f);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getLegend().setWordWrapEnabled(true);

        stringRequest();

        return root;
    }

    private void stringRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl + mKeyWord, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonAry = new JSONArray(response);
                    List<Entry> trendingEntries = new ArrayList<>();
                    for (int i = 0; i < jsonAry.length(); i++) {
                        trendingEntries.add(new Entry(i, jsonAry.getInt(i)));
                    }
                    //Create a LineDataSet for each LineChart and change properties
                    LineDataSet trendingDataSet = new LineDataSet(trendingEntries, "Trending Chart for " + mKeyWord);
                    trendingDataSet.setColor(requireActivity().getColor(R.color.colorPrimaryDark));
                    trendingDataSet.setCircleColor(requireActivity().getColor(R.color.colorPrimaryDark));

                    //Render the chart
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(trendingDataSet);
                    LineData data = new LineData(dataSets);

                    mChart.setData(data);
                    mChart.invalidate(); // refresh
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("stringRequest", "StringRequest error==" + error);
            }
        });
        stringRequest.setTag("stringRequest");
        mRequestQueue.add(stringRequest);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                // do I have to cancel this?
                return true; // -> always yes
            }
        });
    }

}
