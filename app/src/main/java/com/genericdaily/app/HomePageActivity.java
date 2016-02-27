package com.genericdaily.app;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.genericdaily.app.models.Constants;
import com.genericdaily.app.models.MyDataBase;
import com.genericdaily.app.utils.Utils;
import com.genericdaily.app.views.HomePageDrawerView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;

public class HomePageActivity extends AppCompatActivity {

    private HomePageDrawerView homePageDrawerView;
    private boolean dark;
    public RequestQueue requestQueue;
    public JsonObjectRequest objectRequest;
    private MyDataBase myDataBase;
    private HashSet<String> readedSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dark = isDarkTheme();
        if(!dark){
            setTheme(R.style.MyAppTheme_light);
        }else {
            setTheme(R.style.MyAppTheme_Dark);
        }
        setContentView(R.layout.activity_home_page);

        homePageDrawerView = new HomePageDrawerView(this);
        homePageDrawerView.initView();

        myDataBase = MyDataBase.getMyDataBase(this);
        readedSet = new HashSet<>(myDataBase.loadData("Read","id"));
        requestQueue = Volley.newRequestQueue(this);
        objectRequest = new JsonObjectRequest(Constants.Url.THEMES_DAILY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                homePageDrawerView.refrashAdapter(Utils.handleThemeResponse(myDataBase,jsonObject));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                homePageDrawerView.refrashAdapter(myDataBase.loadData("Theme","name"));
            }
        }){

            @Override
            protected Response<JSONObject> parseNetworkResponse(
                    NetworkResponse response) {

                try {
                    JSONObject jsonObject = new  JSONObject(
                            new String(response.data, "UTF-8"));
                    return        Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }

        };
        requestQueue.add(objectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.read_model:
                saveTheme(!dark);
                recreate();
                return true;
            default:
                return homePageDrawerView.getmDrawerToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_homepage_toolbar, menu);
        return true;
    }

    private void saveTheme(boolean dark){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("dark", dark).apply();
    }

    private boolean isDarkTheme(){
        return  PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark",false);
    }

}
