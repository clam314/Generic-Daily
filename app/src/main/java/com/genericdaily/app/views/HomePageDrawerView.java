package com.genericdaily.app.views;



import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.genericdaily.app.R;
import com.genericdaily.app.adapter.DrawerListViewAdapter;
import com.genericdaily.app.fragment.HomePageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页侧滑抽屉的视图
 */
public class HomePageDrawerView {
    private AppCompatActivity mActivity;
    private ListView mListView;
    private View lv_head;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerListViewAdapter drawerListViewAdapter;
    private List<String> list = new ArrayList<>();
    public HomePageDrawerView(AppCompatActivity activity){
        this.mActivity = activity;
    }

    public void initView(){
        mToolbar = (Toolbar)mActivity.findViewById(R.id.toolbar_homepage);
        mActivity.setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout)mActivity.findViewById(R.id.ly_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(mActivity,mDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mListView = (ListView)mActivity.findViewById(R.id.lv_homepage_drawer);
        lv_head = mActivity.getLayoutInflater().inflate(R.layout.ls_drawer_head,null);
        mListView.addHeaderView(lv_head);
        drawerListViewAdapter = new DrawerListViewAdapter(mActivity,list);
        mListView.setAdapter(drawerListViewAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomePageFragment fragment = (HomePageFragment)(mActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_homepage));
                fragment.getRecyclerView().scrollToPosition(0);
                mDrawerLayout.closeDrawers();
            }
        });
    }

    public void refrashAdapter(List<String> strings){
        drawerListViewAdapter.setmList(strings);
        drawerListViewAdapter.notifyDataSetChanged();
    }


    public ActionBarDrawerToggle getmDrawerToggle(){
        return mDrawerToggle;
    }
}
