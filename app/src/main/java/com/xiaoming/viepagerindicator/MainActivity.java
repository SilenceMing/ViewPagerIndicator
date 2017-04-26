package com.xiaoming.viepagerindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Window;

import com.xiaoming.viepagerindicator.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    private List<String> mTitles = Arrays.asList("推荐","首页","分享","推荐","首页","分享","推荐","首页","分享");
    private List<VpSimpleFragment> mContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);

        initViews();
        initDatas();

        //定义顶部Tab的默认数量
        mIndicator.setVisibelCount(4);
        //初始化顶部Tab的标题
        mIndicator.setTabItemTitiles(mTitles);

        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager,0);
    }

    private void initDatas() {
        for(String title : mTitles){
            VpSimpleFragment fragment = VpSimpleFragment.newInstance(title);
            mContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }
            @Override
            public int getCount() {
                return mContents.size();
            }
        };
    }

    private void initViews() {
        mIndicator = (ViewPagerIndicator) findViewById(R.id.ll_indicator);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);
    }
}
