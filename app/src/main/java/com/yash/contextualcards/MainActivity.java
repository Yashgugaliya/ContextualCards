package com.yash.contextualcards;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    Context context;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://run.mocky.io/v3/fefcfbeb-5c12-4722-94ad-b8f92caad1ad";
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APICall(url);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please wait!!");
        progressDialog.setCancelable(false);
        APICall(url);

    }

    void APICall(String url) {
        if(progressDialog != null) {
            progressDialog.show();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            JSONArray cardGroups = result.getJSONArray("card_groups");
                            List<Adapter_InnerList> adapters= new ArrayList<Adapter_InnerList>();
                            for(int i=0;i<cardGroups.length();i++){
                                JSONObject temp = cardGroups.getJSONObject(i);
                                String design_type = temp.getString("design_type");
                                int type = design_type.charAt(2)-'0';
                                boolean scroll = temp.getBoolean("is_scrollable");
                                int height = 80;
                                if(type==9) height = temp.getInt("height");
                                JSONArray cards = temp.getJSONArray("cards");
                                adapters.add(new Adapter_InnerList(cards,type,height,scroll));
                            }
                            recyclerView.setAdapter(new Adapter(adapters));
                            if(progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        swipeRefreshLayout.setRefreshing(false);
    }

}