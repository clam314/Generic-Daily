package com.genericdaily.app.views;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by clam314 on 2016/1/31.
 */
public class MyViewPager extends ViewPager {

    private boolean isStop = false;
    private int scrollTimeOffset = 5000;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void startRoll(final Activity activity){
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isStop) {
                    //每个两秒钟发一条消息到主线程，更新viewpager界面
                    SystemClock.sleep(scrollTimeOffset);
                    if(activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                int newindex = getCurrentItem() + 1;
                                setCurrentItem(newindex);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void setStop(boolean stop){
        isStop = stop;
    }

    public void setScrollTimeOffset(int timeOffset){
        scrollTimeOffset = timeOffset;
    }
}
