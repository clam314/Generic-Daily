package com.genericdaily.app.adapter;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.genericdaily.app.R;
import com.genericdaily.app.ReadNewsActivity;
import com.genericdaily.app.models.MyDataBase;
import com.genericdaily.app.utils.NewsInformation;
import com.genericdaily.app.utils.Utils;
import com.genericdaily.app.views.MyViewPager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by clam314 on 2016/1/31.
 */
public class HomePageRCVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Activity activity;
    private List<NewsInformation> list_rcv;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private RCVViewpagerAdapter viewpagerAdapter;
    private List<NewsInformation> list_vp = new ArrayList<>();
    private HashSet<String> hashSet;
    private MyDataBase myDataBase;


    public HomePageRCVAdapter(Activity activity, List<NewsInformation> list,ImageLoader imageLoader){
        this.activity = activity;
        this.list_rcv = list;
        this.imageLoader = imageLoader;
        inflater = LayoutInflater.from(activity);
        myDataBase = MyDataBase.getMyDataBase(activity);
        hashSet = new HashSet<>(myDataBase.loadData("Read","id"));
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) return 0;

        boolean isRead = hashSet.contains(list_rcv.get(position-1).id);
        String date = list_rcv.get(position-1).date;

        if(!isRead && date != null)  return 1;
        else if(isRead && date != null) return 2;
        else if(!isRead && date == null) return 3;
        else return 4;

    }

    @Override
    public int getItemCount() {
        return list_rcv.size() == 0?list_rcv.size():list_rcv.size()+1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
           final RCVVpViewHolder Holder = new RCVVpViewHolder(inflater.inflate(R.layout.rcv__fg_head, parent, false));
            Holder.viewPager.startRoll(activity);
            return Holder;
        }
        RCVItemDateViewHolder vpHolder = new RCVItemDateViewHolder(inflater.inflate(R.layout.rcv_fg_item_date, parent, false));
        switch (viewType) {
            case 1:
                return vpHolder;
            case 2:
                vpHolder.tv_title.setTextColor(Color.GRAY);
                return vpHolder;
            case 3:
                vpHolder.tv_date.setVisibility(View.GONE);
                return vpHolder;
            default:
                vpHolder.tv_title.setTextColor(Color.GRAY);
                vpHolder.tv_date.setVisibility(View.GONE);
                return vpHolder;
        }



    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof RCVVpViewHolder){
            viewpagerAdapter = new RCVViewpagerAdapter(activity,list_vp,imageLoader, ((RCVVpViewHolder)holder).layout_dot);
            ((RCVVpViewHolder) holder).viewPager.setAdapter(viewpagerAdapter);
            ((RCVVpViewHolder) holder).viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    viewpagerAdapter.pageSelected(position);
                }
            });

        }else if(holder instanceof RCVItemDateViewHolder){

            ((RCVItemDateViewHolder)holder).networkImageView.setImageUrl(list_rcv.get(position - 1).images, imageLoader);
            ((RCVItemDateViewHolder)holder).tv_title.setText(list_rcv.get(position - 1).title);

            if(list_rcv.get(position-1).multipic){
                ((RCVItemDateViewHolder)holder).tv_multipic.setVisibility(View.VISIBLE);
            }else {
                ((RCVItemDateViewHolder)holder).tv_multipic.setVisibility(View.INVISIBLE);
            }
            if(position == 1){
                ((RCVItemDateViewHolder)holder).tv_date.setText("今日热闻");
            } else {
                ((RCVItemDateViewHolder)holder).tv_date.setText(list_rcv.get(position - 1).date);
            }
            ((RCVItemDateViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDataBase.saveWeatheridRead(list_rcv.get(position - 1).id);
                    Intent intent = new Intent(activity, ReadNewsActivity.class);
                    intent.putStringArrayListExtra("idList", Utils.getIdArrayList(list_rcv));
                    intent.putExtra("id",list_rcv.get(position-1).id);
                    activity.startActivity(intent);
                }
            });
        }
    }



    public String getItemDate(int position){
        return list_rcv.get(position).dateFormat;
    }

    public void setList_rcv(List<NewsInformation> list){
        list_rcv = list;
    }

    public void addList_rcv(List<NewsInformation> list){
        list_rcv.addAll(list);
    }

    public void setList_vp(List<NewsInformation> list){
        list_vp = list;
    }


    private class RCVItemDateViewHolder extends RecyclerView.ViewHolder {
        private NetworkImageView networkImageView;
        private TextView tv_title;
        private TextView tv_multipic;
        private TextView tv_date;
        private CardView cardView;
        public RCVItemDateViewHolder(View itemView) {
            super(itemView);
            networkImageView = (NetworkImageView)itemView.findViewById(R.id.iv_rcv_item_home);
            tv_multipic = (TextView)itemView.findViewById(R.id.tv_rcv_multipic);
            tv_title = (TextView)itemView.findViewById(R.id.tv_rcv_item);
            tv_date = (TextView)itemView.findViewById(R.id.tv_rcv_date);
            cardView = (CardView)itemView.findViewById(R.id.cv_rcv_item);
        }
    }

    private class RCVVpViewHolder extends RecyclerView.ViewHolder {
        public MyViewPager viewPager;
        protected LinearLayout layout_dot;
        public RCVVpViewHolder(View itemView) {
            super(itemView);
            viewPager = (MyViewPager)itemView.findViewById(R.id.viewpager_home_fg);
            layout_dot = (LinearLayout)itemView.findViewById(R.id.ll_dot_group);
        }
    }
}
