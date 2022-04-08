package com.daily.gccollector;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daily.gccollector.Adapters.BinItemAdapter;
import com.daily.gccollector.Models.BinMaster;
import com.daily.gccollector.Utility.AppConstraint;
import com.daily.gccollector.Volley.VolleyClient;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BinMasterActivityList extends AppCompatActivity {
    ArrayList<BinMaster> itemsList;
    BinItemAdapter binItemAdapter;
    RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin_master_list);
        setTitle("Bin List");
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetAllBins();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        GetAllBins();
    }
    private void GetAllBins() {
        swipeContainer.setRefreshing(true);
        itemsList= new ArrayList<>();
        try {

            JSONObject obj = new JSONObject();
            obj.put("where",null);
            obj.put("orderby",null);
            obj.put("pageNo",null);
            obj.put("pageSize",null);
            obj.put("userId",null);
            obj.put("parameterValues",null);

            RequestQueue queue = VolleyClient.getInstance(BinMasterActivityList.this).getRequestQueue();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AppConstraint.GET_ALL_BIN,obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try
                            {
                                if(response.getInt("status")==1)
                                {
                                    JSONArray data = response.getJSONArray("data");

                                    for(int i=0; i<data.length();i++)
                                    {
                                        JSONObject obj = data.getJSONObject(i);
                                        BinMaster bin = new BinMaster();
                                        bin.setBinLocID(obj.getInt("binLocID"));
                                        bin.setBinLocName(obj.getString("binLocName"));
                                        bin.setBinLocCode(obj.getString("binLocCode"));
                                        bin.setDescription(obj.getString("description"));
                                        bin.setLocImage(obj.getString("locImage"));
                                        itemsList.add(bin);
                                    }
                                    recyclerView= (RecyclerView)findViewById(R.id.recycleView);
                                    binItemAdapter = new BinItemAdapter(itemsList);
                                    recyclerView.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(binItemAdapter);
                                }
                                else
                                {
                                    Toast.makeText(BinMasterActivityList.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                Toast.makeText(BinMasterActivityList.this,ex.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            swipeContainer.setRefreshing(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(BinMasterActivityList.this,"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(BinMasterActivityList.this, "Error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
        searchView.setVisibility(View.VISIBLE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>0)
                {
                    binItemAdapter.getFilter().filter(newText);
                }
                else
                {
                    GetAllBins();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            SharedPreferences sharedpreferences = getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(AppConstraint.PRF_TOKEN, "");
            editor.putString(AppConstraint.PRF_USER, "");
            editor.putString(AppConstraint.PRF_DEVICENO, "");
            editor.putString(AppConstraint.PRF_VEHICLENO, "");
            editor.commit();
            Intent intent = new Intent(BinMasterActivityList.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(BinMasterActivityList.this,"Logout Successfully!!",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
