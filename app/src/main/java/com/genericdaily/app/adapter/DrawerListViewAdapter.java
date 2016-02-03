package com.genericdaily.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.genericdaily.app.R;

import java.util.List;

/**
 *
 */
public class DrawerListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private ViewHolder mViewHolder;
    public DrawerListViewAdapter(Context context, List<String> list){
        this.mContext = context;
        this.mList = list;
    }


    public void setmList(List<String> list){
        mList = list;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mViewHolder = null;
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ls_drawer_item,null);
            mViewHolder.mTextView = (TextView)convertView.findViewById(R.id.tv_drawer_ls_item);
            convertView.setTag(mViewHolder);
        }else {
            mViewHolder = (ViewHolder)convertView.getTag();
        }
        mViewHolder.mTextView.setText(mList.get(position));
        return convertView;
    }

    private class ViewHolder{
        private TextView mTextView;
    }
}
