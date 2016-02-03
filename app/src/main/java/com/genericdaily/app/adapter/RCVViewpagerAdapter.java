package com.genericdaily.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.genericdaily.app.R;
import com.genericdaily.app.ReadNewsActivity;
import com.genericdaily.app.utils.NewsInformation;
import com.genericdaily.app.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clam314 on 2016/1/31.
 */
public class RCVViewpagerAdapter extends PagerAdapter {
    private List<NewsInformation> informationList;
    private Context context;
    private List<View> viewContainer;
    private LinearLayout layout_dot;
    private ImageLoader imageLoader;
    private int preDotPosition;

    public RCVViewpagerAdapter(Context context, List<NewsInformation> list, ImageLoader imageLoader,LinearLayout layout_dot) {

        this.informationList = list;
        this.context = context;
        this.imageLoader = imageLoader;
        this.layout_dot = layout_dot;
        this.viewContainer = createViewContainer(list);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewContainer.get(position % viewContainer.size()));
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        View view = viewContainer.get(position % viewContainer.size());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadNewsActivity.class);
                intent.putExtra("id",informationList.get(position%viewContainer.size()).id);
                intent.putExtra("idList", Utils.getIdArrayList(informationList));
                context.startActivity(intent);
            }

        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return informationList.size() !=0 ? Integer.MAX_VALUE:0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public List<View> createViewContainer(List<NewsInformation> list) {
        View view = null;
        if(list != null){
        viewContainer = new ArrayList<>();
        for (NewsInformation information : list) {
            view = LayoutInflater.from(context).inflate(R.layout.viewpager_home,null);
            NetworkImageView networkImageView = (NetworkImageView)view.findViewById(R.id.iv_vp_home);
            TextView textView = (TextView)view.findViewById(R.id.tv_vp_home);
            networkImageView.setImageUrl(information.images,imageLoader);
            textView.setText(information.title);
            viewContainer.add(view);
        }
        layout_dot.removeAllViews();
        drawDotGroup(list.size());
        }
        return viewContainer;
    }

    public void drawDotGroup(int size){
        View dot = null;
        LinearLayout.LayoutParams params = null;
        for (int i = 0;i<size;i++) {
            // 每循环一次添加一个点到线行布局中
            dot = new View(context);
            dot.setBackgroundResource(R.drawable.dot_bg_selector);
            params = new LinearLayout.LayoutParams(20,20);
            params.leftMargin = 10;
            dot.setEnabled(false);
            dot.setLayoutParams(params);
            layout_dot.addView(dot); // 向线性布局中添加"点"
        }
    }


    public void pageSelected(int position) {
        // 取余后的索引，得到新的page的索引
        int newPositon = position % viewContainer.size();
        // 把上一个点设置为被选中
        layout_dot.getChildAt(preDotPosition).setEnabled(false);
        // 根据索引设置那个点被选中
        layout_dot.getChildAt(newPositon).setEnabled(true);
        // 新索引赋值给上一个索引的位置
        preDotPosition = newPositon;
    }
}
