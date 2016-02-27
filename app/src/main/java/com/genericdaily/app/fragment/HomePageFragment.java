package com.genericdaily.app.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.genericdaily.app.R;
import com.genericdaily.app.adapter.HomePageRCVAdapter;
import com.genericdaily.app.models.Constants;
import com.genericdaily.app.utils.NewsInformation;
import com.genericdaily.app.utils.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by clam314 on 2016/1/30.
 */
public class HomePageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomePageRCVAdapter homePageRCVAdapter;
    private List<NewsInformation> informationList;
    private LinearLayoutManager linearLayoutManager;
    private Calendar calendar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(getActivity());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruCache<String,Bitmap> cache;
            {
                int maxCache = 10*1024*1024;
                cache = new LruCache<String ,Bitmap>(maxCache){
                    @Override
                    protected int sizeOf(String  key, Bitmap bitmap) {
                        return bitmap.getRowBytes() * bitmap.getHeight();
                    }
                };
            }
            @Override
            public Bitmap getBitmap(String s) {
                return cache.get(s);
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {
                cache.put(s,bitmap);
            }
        });
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        View view = inflater.inflate(R.layout.fragment_home_page,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.rcv_fg_hp);
        informationList = new ArrayList<>();
        homePageRCVAdapter = new HomePageRCVAdapter(getActivity(),informationList,imageLoader);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RcycleViewScrollListioner());
        recyclerView.setAdapter(homePageRCVAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.sly__fragment);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        requestHotNews();

        return view;
    }

    private void requestHotNews(){
        JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.Url.HOT_NEWS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                homePageRCVAdapter.setList_rcv(Utils.handleNewsResponse(jsonObject, true));
                homePageRCVAdapter.setList_vp(Utils.handleHotNewsResponse(jsonObject));
               // homePageRCVAdapter.notifyItemChanged(0);
                homePageRCVAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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
    public void onRefresh() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.Url.HOT_NEWS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                homePageRCVAdapter.setList_rcv(Utils.handleNewsResponse(jsonObject, true));
                homePageRCVAdapter.setList_vp(Utils.handleHotNewsResponse(jsonObject));
                homePageRCVAdapter.notifyItemChanged(0);
                homePageRCVAdapter.notifyDataSetChanged();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,1);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(),"没有网络",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
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


    public  RecyclerView getRecyclerView(){
        return recyclerView;
    }

    private class RcycleViewScrollListioner extends RecyclerView.OnScrollListener{
        private boolean loading = false;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            Log.i("loading","");
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            final int totalItemCounnt = linearLayoutManager.getItemCount();

            Toolbar toolbar =(Toolbar) getActivity().findViewById(R.id.toolbar_homepage);
           if(firstVisibleItem == 0) toolbar.setTitle("首页");
            else {
               toolbar.setTitle(homePageRCVAdapter.getItemDate(firstVisibleItem-1));
           }

            if(lastVisibleItem == totalItemCounnt - 1 && !loading){
                String before = Utils.dateformat(calendar);
                Log.i("loading","date: "+before);
                loading = true;
                JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.Url.OLD_NEWS+before, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        homePageRCVAdapter.addList_rcv(Utils.handleNewsResponse(jsonObject,false));
                        homePageRCVAdapter.notifyItemInserted(totalItemCounnt);
                        loading = false;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(),"没有网络",Toast.LENGTH_SHORT).show();
                        loading = false;
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
        }
    }

}
