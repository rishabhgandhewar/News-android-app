package com.example.newsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.newsapp.ui.MySingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queryString=(String)parent.getItemAtPosition(position);
                searchAutoComplete.setText("" + queryString);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchableActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadSuggestions(newText);
                return false;
            }
        });
        return true;
    }

    private void loadSuggestions(String newText){
        String url = "https://api.cognitive.microsoft.com/bing/v7.0/suggestions?mkt=fr-FR&q="+newText;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray suggestionGroups = response.getJSONArray("suggestionGroups");
                            JSONObject object1 = suggestionGroups.getJSONObject(0);
                            JSONArray searchSuggestions = object1.getJSONArray("searchSuggestions");
                            ArrayList<String> myList = new ArrayList<>();
                            int n = searchSuggestions.length();
                            if(searchSuggestions.length() > 5)
                                n = 5;
                            for(int i = 0; i < n; i++){
                                JSONObject sugg = searchSuggestions.getJSONObject(i);
                                String str = sugg.getString("displayText");
                                myList.add(str);
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,  android.R.layout.simple_dropdown_item_1line, myList);
                            arrayAdapter.getFilter().filter(searchAutoComplete.getText(), null);
                            searchAutoComplete.setThreshold(3);
                            searchAutoComplete.setAdapter(arrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Ocp-Apim-Subscription-Key", "38867f004a3f4e8897f3bbf51d9628f4");
                return headers;
            }
        };
        System.out.println(jsonObjectRequest);
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest,"headerRequest");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_headline, R.id.navigation_trending, R.id.navigation_bookmark)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
