package com.example.NewsApp;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.NewsApp.ui.search.SearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityInterface{

    private final int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude = 33, mLongitude = -118;
    private RequestQueue mRequestQueue;

    static String subscriptionKey = "9553c50d7e5b448fa8eb0e6968e44421";
    static String host = "https://mybingsuggest.cognitiveservices.azure.com/";
    static String path = "bing/v7.0/suggestions?q=";

    private FragmentRefreshListener fragmentRefreshListener;

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh() throws IOException;
    }

    @Override
    public double getDoubleLatitude(){ return mLatitude;}
    @Override
    public double getDoubleLongitude(){ return mLongitude;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mRequestQueue = Volley.newRequestQueue(getBaseContext());
        getLastLocation();

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Resources resources = getResources();
        final MenuItem item = menu.findItem(R.id.search_view);
        final SearchView searchView = (SearchView) item.getActionView();
        int completeTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView completeText = (AutoCompleteTextView) searchView
                .findViewById(completeTextId) ;
        completeText.setThreshold(0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.putExtra("keyword", query);
                intent.setClass(Objects.requireNonNull(getBaseContext()), SearchActivity.class);
                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() < 3) {
                    return false;
                }

                StringRequest request = new StringRequest(Request.Method.GET, host + path + newText, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals(null)) {
                            Log.e("Your Array Response", response);
                            ArrayList<String> suggestionArray = new ArrayList<String>();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray suggestionGroups =  jsonObject.getJSONArray("suggestionGroups");
                                JSONArray searchSuggestions =  suggestionGroups.getJSONObject(0).getJSONArray("searchSuggestions");
                                for (int i = 0; i < searchSuggestions.length(); i++) {
                                    suggestionArray.add(searchSuggestions.getJSONObject(i).getString("query"));
                                }
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, suggestionArray);
                                completeText.setAdapter(dataAdapter);
                                completeText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                                        searchView.setQuery(suggestionArray.get(position),true);
                                    }

                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.e("Your Array Response", "Data Null");
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error is ", "" + error);
                    }
                }) {

                    //This is for Headers If You Needed
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("Ocp-Apim-Subscription-Key", subscriptionKey);
                        return params;
                    }

                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request);

                return false;
            }
        });
        return true;
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    requestNewLocationData();
                                    mLatitude = location.getLatitude();
                                    mLongitude = location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
            if(getFragmentRefreshListener()!= null){
                try {
                    getFragmentRefreshListener().onRefresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}