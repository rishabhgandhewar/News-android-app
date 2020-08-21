package com.example.newsapp.ui.trending;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {
    private LineChart lineChart;
    //private SearchView searchView;
    private EditText editText;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList<Entry> lineEntries;
    String toSearch;
    private String url = "http://csci571-newsandroid.us-east-1.elasticbeanstalk.com/trends?query=";
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trending, container, false);
        toSearch = "coronavirus";
        loadData();
        lineChart = root.findViewById(R.id.lineChart);
        editText = root.findViewById(R.id.trendSearch);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_DONE){

                    toSearch = v.getText().toString();
                    loadData();
                    return true;
                }
                return false;
            }
        });
//        searchView = root.findViewById(R.id.trendSearch);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                StringBuilder stringBuilder = new StringBuilder(url);
//                stringBuilder.append(query);
//                toSearch = query;
//                url = stringBuilder.toString();
//                loadData();
//                return false;
//            }

//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        return root;
    }
    public void loadData(){
        StringBuilder stringBuilder = new StringBuilder(url);
        stringBuilder.append(toSearch);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, stringBuilder.toString(),
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("values");
                            lineEntries = new ArrayList<>();
                            for(int i = 0; i < jsonArray.length(); i++){
                                lineEntries.add(new Entry(i,jsonArray.getInt(i)));
                            }
                            lineDataSet = new LineDataSet(lineEntries, "Trending chart for "+ toSearch);
                            lineDataSet.setColor(requireActivity().getColor(R.color.colorPrimaryDark));
                            lineData = new LineData(lineDataSet);
                            lineChart.setData(lineData);
                            lineDataSet.setCircleColor(requireActivity().getColor(R.color.colorPrimaryDark));
                            lineDataSet.setCircleHoleColor(requireActivity().getColor(R.color.colorPrimaryDark));
                            lineChart.getAxisLeft().setDrawGridLines(false);
                            lineChart.getAxisLeft().setAxisLineColor(Color.WHITE);
                            lineChart.getXAxis().setDrawGridLines(false);
                            lineChart.getAxisRight().setDrawGridLines(false);
                            Legend l = lineChart.getLegend();
                            l.setYOffset(10f);
                            l.setTextSize(15f);
                            l.setWordWrapEnabled(true);
                            lineChart.invalidate();
                        }
                        catch (JSONException e) {
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
}
