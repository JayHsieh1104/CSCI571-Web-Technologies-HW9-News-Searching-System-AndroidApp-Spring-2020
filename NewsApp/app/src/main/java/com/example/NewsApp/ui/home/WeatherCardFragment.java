package com.example.NewsApp.ui.home;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.NewsApp.MainActivity;
import com.example.NewsApp.MainActivityInterface;
import com.example.NewsApp.R;
import com.example.NewsApp.ui.CustomImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class WeatherCardFragment extends Fragment {
    private final String TAG = "WeatherCardFragment";
    private String mState = "California";
    private String mCity = "Los Angeles";
    private String mDegree = "14°C";
    private String mWeather = "Clouds";
    private String openWeatherMapApiKey = "f0fd8407da49f58672e7cafd600130f2";
    private String url;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private RequestQueue mRequestQueue;
    private Geocoder geocoder;

    private TextView mTextState;
    private TextView mTextCity;
    private TextView mTextDegree;
    private TextView mTextWeather;
    private ImageView mWeatherImage;

    MainActivityInterface mListener;

    public WeatherCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityInterface) {
            mListener = (MainActivityInterface) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_weather_card, container, false);
        mTextState = root.findViewById(R.id.weatherCardState);
        mTextCity = root.findViewById(R.id.weatherCardCity);
        mTextDegree = root.findViewById(R.id.weatherCardDegree);
        mTextWeather = root.findViewById(R.id.weatherCardWeather);
        mWeatherImage = root.findViewById(R.id.weatherCardImage);

        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() throws IOException {
                mLatitude = mListener.getDoubleLatitude();
                mLongitude = mListener.getDoubleLongitude();
                List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
                String cityName = addresses.get(0).getLocality();
                String stateName = addresses.get(0).getAdminArea();
                mState = stateName;
                mCity = cityName;
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=" + openWeatherMapApiKey;
                new Task().execute();
            }
        });
//        Toast.makeText(getContext(), "oncreate", Toast.LENGTH_SHORT).show();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLatitude = mListener.getDoubleLatitude();
        mLongitude = mListener.getDoubleLongitude();
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityName = addresses.get(0).getLocality();
        String stateName = addresses.get(0).getAdminArea();
        mState = stateName;
        mCity = cityName;
        url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&appid=" + openWeatherMapApiKey;
        new Task().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        JSONArray weather_main = jsonObj.getJSONArray("weather");
                        String weather = weather_main.getJSONObject(0).getString("main");
                        String temperature = jsonObj.getJSONObject("main").getString("temp");
                        mDegree = temperature.substring(0,2) + "°C";
                        mWeather = weather;
                        mTextState.setText(mState);
                        mTextCity.setText(mCity);
                        mTextDegree.setText(mDegree);
                        mTextWeather.setText(mWeather);
                        if(mWeather.equals("Clear")) {
                            mWeatherImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.clear_weather, null));
                        }
                        else if (mWeather.equals("Snow")) {
                            mWeatherImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.snowy_weather, null));
                        }
                        else if (mWeather.equals("Rain")) {
                            mWeatherImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rainy_weather, null));
                        }
                        else if (mWeather.equals("Drizzle")) {
                            mWeatherImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rainy_weather, null));
                        }
                        else if (mWeather.equals("Thunderstorm")) {
                            mWeatherImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.thunder_weather, null));
                        }
                        else {
                            mWeatherImage.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cloudy_weather, null));
                        }
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

}
