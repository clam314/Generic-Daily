package com.genericdaily.app.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.genericdaily.app.R;
import com.genericdaily.app.ReadNewsActivity;
import com.genericdaily.app.models.Constants;
import com.genericdaily.app.utils.NewsContent;
import com.genericdaily.app.utils.StoryExtra;
import com.genericdaily.app.utils.Utils;

import org.json.JSONObject;

/**
 * Created by clam314 on 2016/2/1.
 */
public class ReadNewsFragment extends Fragment implements NestedScrollView.OnScrollChangeListener ,Response.Listener<JSONObject>,Response.ErrorListener{
    private Toolbar toolbar;
    private NetworkImageView imageView;
    public NestedScrollView nestedScrollView;
    private WebView webView;
    private TextView tv_title;
    private TextView tv_supporter;
    private ImageLoader imageLoader;
    private  float wv_hight;
    private  boolean first = true;
    private RelativeLayout relativeLayout;
    private TextView tv_comment;
    private TextView tv_popularity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_news, container, false);
        webView = (WebView) view.findViewById(R.id.wv_read_news);
        webView.setFocusable(false);
        relativeLayout = (RelativeLayout)view.findViewById(R.id.rl_read_news);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_read_news);
        tv_comment = (TextView)toolbar.findViewById(R.id.tb_comment);
        tv_popularity = (TextView)toolbar.findViewById(R.id.tb_popularity);
        imageView = (NetworkImageView)view.findViewById(R.id.iv_read_news);
        tv_title = (TextView)view.findViewById(R.id.tv_title);
        tv_supporter = (TextView)view.findViewById(R.id.tv_support);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.sv_read_news);
        nestedScrollView.setOnScrollChangeListener(this);
        webView.setBackgroundColor(Color.DKGRAY);
        return view;
    }


    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        int[] wv_lenght = new int[2];
        webView.getLocationInWindow(wv_lenght);
        if(first){
            wv_hight = wv_lenght[1];
            first = false;
        }
        if(wv_lenght[1] < 0 && scrollY < oldScrollY){
            toolbar.setAlpha(0.9f);
        }else {
            toolbar.setAlpha((wv_lenght[1]-250)/(wv_hight-250));
        }
        relativeLayout.setTranslationY((400 / wv_hight) * wv_lenght[1] - 400);

    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        NewsContent newsContent = Utils.handleNewsContent(jsonObject);
        tv_title.setText(newsContent.title);
        tv_supporter.setText(newsContent.image_source);
        imageView.setImageUrl(newsContent.image, imageLoader);
        questStoryExtra(newsContent.id);

        String css = getCss();
        String html = "<html><head>" + css + "</head><body>" + newsContent.body + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        webView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    private String getCss(){
        boolean dark = isDarkTheme();
        if(!dark){
           return  "<link rel=\"stylesheet\" href=\"file:///android_asset/news.css\" type=\"text/css\">";
        }else {
            return  "<link rel=\"stylesheet\" href=\"file:///android_asset/newsdark.css\" type=\"text/css\">";
        }
    }

    public void questNewsContent(String id,RequestQueue requestQueue , ImageLoader imageLoader ){
        this.imageLoader = imageLoader;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.Url.NEWS_CONTENT+id,null,this,this);
        requestQueue.add(objectRequest);
    }

    private boolean isDarkTheme(){
        return  PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("dark",false);
    }

    private void questStoryExtra(String id){
       JsonObjectRequest objectRequest = new JsonObjectRequest(Constants.Url.EXTRA_INFORMATION + id, null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject jsonObject) {
               StoryExtra storyExtra = Utils.handleStoryExtra(jsonObject);
               tv_popularity.setText(storyExtra.popularity);
               tv_comment.setText(storyExtra.comments);
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError volleyError) {

           }
       });
        ((ReadNewsActivity)getActivity()).getRequestQueue().add(objectRequest);
    }
}
