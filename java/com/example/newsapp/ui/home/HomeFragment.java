package com.example.newsapp.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp.MainActivity;
import com.example.newsapp.R;
import com.example.newsapp.ui.HomeAdapter;
import com.example.newsapp.ui.News;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Location location;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FusedLocationProviderClient client;
    private GoogleApiClient googleApiClient;
    private HomeViewModel homeViewModel;
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<JSONArray> data = new ArrayList<>();
    private ArrayList<News> newsItem = new ArrayList<>();
    private ArrayList<String> token = new ArrayList<>();
    private boolean fromInternalActivity = false;
    private final String url = "http://csci571-newsandroid.us-east-1.elasticbeanstalk.com/guardian";
    String city;
    String state;
    String description;
    static View root;
    private static final String TAG = MainActivity.class.getSimpleName();
    @AfterPermissionGranted(123)
    public void getPermission(){
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            googleApiClient = new GoogleApiClient.Builder(getContext()).
                    addApi(LocationServices.API).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).build();
            client = LocationServices.getFusedLocationProviderClient(getContext());
            client.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!= null){
                        String loc = location.toString();
                        String lat = loc.substring(15,22);
                        String lon = loc.substring(25,34);
                    }
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, "We require Location permission for Weather information", 123, perms);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    static long zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2, ChronoUnit unit){
        return unit.between(d1, d2);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fromInternalActivity = false;
        root = inflater.inflate(R.layout.fragment_home, container, false);
        getPermission();
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.rgb(56, 3, 153));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod()
                mSwipeRefreshLayout.setRefreshing(true);
                getPermission();
                newsItem = new ArrayList<>();
                loadNews();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView = (RecyclerView) root.findViewById(R.id.home_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        return root;
    }

    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fromInternalActivity = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(fromInternalActivity) {
            System.out.println("Resuming");
            mSwipeRefreshLayout.setRefreshing(true);
            newsItem = new ArrayList<>();
            loadNews();
            mSwipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }

        fromInternalActivity = false;

    }

    public void loadNews() {
        final ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.progressBarHome);
        final TextView fetching = (TextView) root.findViewById(R.id.fetching);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
                    public void onResponse(String response) {
                        newsItem = new ArrayList<>();
                        progressBar.setVisibility(View.GONE);
                        fetching.setVisibility(View.GONE);
                        try {
                            JSONArray array = new JSONArray(response);
                            int len = array.length();
                            for(int i = 0; i < array.length(); i++){
                                JSONObject newsObject = array.getJSONObject(i);
                                LocalDateTime ldt = LocalDateTime.now();
                                System.out.println(ldt);//Local date time
                                ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );
                                ZonedDateTime LA = ldt.atZone( zoneId );
                                Instant now = Instant.parse(newsObject.getString("published_date"));
                                ZonedDateTime newsTime = now.atZone( zoneId );
                                System.out.println(i+" news date "+newsTime);
                                System.out.println(i+" now time "+LA);
                                long diff = zonedDateTimeDifference(newsTime, LA, ChronoUnit.SECONDS);
                                String timeAgo = "";
                                if (diff < 60) {
                                    timeAgo = diff+"s ago";
                                } else if (diff < 60 * 60) {
                                    timeAgo = diff/60+"m ago";
                                } else if (diff < 60 * 60 * 24) {
                                    timeAgo = diff/3600+"h ago";
                                } else {
                                    timeAgo = diff/86400+"d ago";
                                }
                                News news = new News(newsObject.getString("title"), newsObject.getString("id"), timeAgo, newsObject.getString("section"),newsObject.getString("image"), newsObject.getString("url"), newsObject.getString("published_date"));
                                newsItem.add(news);
                            }
                            adapter = new HomeAdapter(newsItem, getContext(), 0);
                            if (0 == recyclerView.getItemDecorationCount()) {
                                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                            }
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void onConnected(@Nullable Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        double lat = 0;
        double lon = 0;

        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            DecimalFormat df = new DecimalFormat(".####");
            String l1 = df.format(lat);
            String l2 = df.format(lon);
            lat = Double.parseDouble(l1);
            lon = Double.parseDouble(l2);
        }
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String cityName = addresses.get(0).getLocality();
        final String stateName = addresses.get(0).getAdminArea();
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&units=metric&appid=b2097491043b1835d3ead945680d1503";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray weather = response.getJSONArray("weather");
                            JSONObject w1 = weather.getJSONObject(0);
                            String desc = w1.getString("description");
                            String title = w1.getString("main");
                            JSONObject main = response.getJSONObject("main");
                            String temp = main.getString("temp");
                            double t = Math.floor(Double.parseDouble(temp));
                            int temp1 = (int) t;
                            city = cityName;
                            state = stateName;
                            temp = ""+temp1;
                            description = desc;
                            TextView textview = root.findViewById(R.id.city);
                            textview.setText(city);
                            textview = root.findViewById(R.id.state);
                            textview.setText(state);
                            textview = root.findViewById(R.id.temperature);
                            textview.setText(temp+"\u2103");
                            textview = root.findViewById(R.id.desc);
                            textview.setText(title);
                            LinearLayout img = root.findViewById(R.id.imageView1);
//                            img.setImageResource(R.drawable.);
                            if(title.equals("Clouds")){
                                img.setBackgroundResource(R.drawable.cloudy);
                            }
                            else if(title.equals("Clear")) {
                                img.setBackgroundResource(R.drawable.clear_weather);
                            }
                            else if(title.equals("Snow")){
                                img.setBackgroundResource(R.drawable.snowy_weather);
                            }
                            else if(title.equals("Rain") || title.equals("drizzle")){
                                img.setBackgroundResource(R.drawable.rainy_weather);
                            }
                            else if(title.equals("Thunderstorm")){
                                img.setBackgroundResource(R.drawable.thunder_weather);
                            }
                            else{
                                img.setBackgroundResource(R.drawable.sunny_weather);
                            }
                            LinearLayout l = (LinearLayout) root.findViewById(R.id.weatherLayout);
                            newsItem = new ArrayList<>();
                            l.setVisibility(View.VISIBLE);
                            loadNews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        System.out.println(jsonObjectRequest);
//        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    googleApiClient = new GoogleApiClient.Builder(getContext()).
                            addApi(LocationServices.API).
                            addConnectionCallbacks(this).
                            addOnConnectionFailedListener(this).build();
                    client = LocationServices.getFusedLocationProviderClient(getContext());
                    client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                googleApiClient.connect();
                                loadNews();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
