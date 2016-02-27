package com.genericdaily.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.genericdaily.app.adapter.ReadNewsVPAdapter;
import com.genericdaily.app.fragment.ReadNewsFragment;
import com.genericdaily.app.models.Constants;
import com.genericdaily.app.models.MyDataBase;
import com.genericdaily.app.utils.NewsInformation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadNewsActivity extends AppCompatActivity  {
    private Toolbar toolbar;
    private RequestQueue requestQueue;
    private ViewPager viewPager;
    private ReadNewsVPAdapter adapter;
    private List<Fragment> fragmentList;
    private ImageLoader imageLoader;
    private int startPosition;
    private String id;
    private ArrayList<String> list;
    private MyDataBase myDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheTheme();
        setContentView(R.layout.activity_read_news);

        myDataBase = MyDataBase.getMyDataBase(this);
        requestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String s) {
                return null;
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar_read_news);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        list = intent.getStringArrayListExtra("idList");
        Log.i("listsize","listsize: "+list.size());
        startPosition  = list.indexOf(id);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        fragmentList = new ArrayList<>();
        for(int i = 0;i < list.size(); i++) {
            fragmentList.add(new ReadNewsFragment());
        }
        ReadNewsFragment fragment = (ReadNewsFragment)fragmentList.get(startPosition);
        fragment.questNewsContent(id,requestQueue,imageLoader);

        adapter = new ReadNewsVPAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new PageChangelistener());
        viewPager.setCurrentItem(startPosition);

    }

    private class PageChangelistener extends ViewPager.SimpleOnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
            ReadNewsFragment fragment = (ReadNewsFragment) fragmentList.get(position);
           // fragment.nestedScrollView.smoothScrollTo(0, toolbar.getHeight());
            fragment.questNewsContent(list.get(position), requestQueue, imageLoader);
            myDataBase.saveWeatheridRead(list.get(position));
            adapter.notifyDataSetChanged();
            fragment = null;
        }
    }



    private void setTheTheme(){
        boolean dark = isDarkTheme();
        if(!dark){
            setTheme(R.style.MyAppTheme_light);
        }else {
            setTheme(R.style.MyAppTheme_Dark);
        }
    }

    private boolean isDarkTheme(){
        return  PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark",false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewPager.removeAllViews();
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}