package com.genericdaily.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.genericdaily.app.models.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private TextView mTextView;
    private NetworkImageView mNetworkImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
            @Override
            public Bitmap getBitmap(String s) {
                return null;
            }

            @Override
            public void putBitmap(String s, Bitmap bitmap) {

            }
        });

        mTextView = (TextView)findViewById(R.id.start_activity_tv);
        mNetworkImageView = (NetworkImageView)findViewById(R.id.start_activity_iv);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.Url.START_IMAGE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
               try {
                   if (jsonObject.has("text")) {
                       mTextView.setText(jsonObject.getString("text"));
                   }
                   if(jsonObject.has("img")){
                       mNetworkImageView.setImageUrl(jsonObject.getString("img"),mImageLoader);
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
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

        mRequestQueue.add(jsonObjectRequest);
        startAnim(mNetworkImageView);
    }

    private void startAnim(ImageView v){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v,"scaleX",1f,1.15f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v,"scaleY", 1f, 1.15f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(3000);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent homeIntent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        });
        animatorSet.start();
    }
}
