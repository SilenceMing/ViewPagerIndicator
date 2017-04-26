package com.xiaoming.viepagerindicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoming.viepagerindicator.R;

import java.util.List;

/**
 * @author Slience_Manager
 * @time 2017/4/25 13:15
 */

public class ViewPagerIndicator extends LinearLayout {
    //画笔
    private Paint mPaint;
    //绘制三角形的三条线
    private Path mPath;
    //三角形的宽高
    private int mTriangleWidth;
    private int mTriangleHeight;
    //三角形宽度和Tab长度的比例，为了适应不同屏幕的比例
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;
    //限制三角形的最大高度
    private final int DIMENSION_TRIAGNGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);
    //三角形初始化的偏移位置
    private int mInitTriangleX;
    //三角形移动时的位置
    private int mTriangleX;
    //Tab在页面的可见数量
    private int mTabVisibleCount;
    //默认Tab在页面的可见数量
    private static final int COUNT_DEFAULT_TAB = 4;

    private List<String> mTitles;
    //Tab默认颜色
    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    //Tab选中的高亮颜色
    private static final int COLOR_TEXT_HIGHLIGHT = 0xFFFFFFFF;

    private ViewPager mViewPager;



    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //获取可见的Tab的数量
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visibel_tab_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        a.recycle();


        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#ffffff"));
        //设置三角形各个定点的圆角
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0)
            return;
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;

            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 进行三角形的绘制
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTriangleX + mTriangleX, getHeight() + 2);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 以已知的宽度和高度去设置控件的宽高，当控件的宽高发生变化时会调用此方法  --> 设置三角形的大小
     *
     * @param w    顶部Tab的总长度
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
        //限制其最大高度
        mTriangleWidth = Math.min(mTriangleWidth,DIMENSION_TRIAGNGLE_WIDTH_MAX);
        mInitTriangleX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;

        initTriangle();
    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {
        mTriangleHeight = mTriangleWidth / 2;

        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();

    }

    /**
     * 指示器跟随手指进行滚动
     *
     * @param position
     * @param Offset
     */
    public void scroll(int position, float Offset) {
        int TabWidth = getWidth() / mTabVisibleCount;
        mTriangleX = (int) (TabWidth * (position + Offset));

        //当content滑动的时候，通知Tab标签滑动
        if (position >= (mTabVisibleCount - 2) && Offset > 0 && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * TabWidth + (int) (TabWidth * Offset), 0);
            } else {
                this.scrollTo(position * TabWidth + (int) (TabWidth * Offset), 0);
            }
        }
        //让三角形进行重绘
        invalidate();
    }

    public void setTabItemTitiles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTitles = titles;
            for (String title : titles) {
                addView(generateTextView(title));
            }
            //Tab的点击事件
            setItemClickEvent();
        }
    }

    /**
     * 设置可见Tab的数量
     *
     * @param count
     */
    public void setVisibelCount(int count) {
        mTabVisibleCount = count;
    }

    /**
     * 根据title创建Tab
     *
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        return tv;
    }


    /**
     * 为用户突出ViewPager的页面变化的监听接口
     */
    public interface PageOnChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public PageOnChangeListener mListener;
    public void setOnPageChangeLinstener(PageOnChangeListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置关联的ViewPager
     *
     * @param viewPager
     * @param position
     */
    public void setViewPager(ViewPager viewPager, int position) {
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //三角形滑动的距离 TabWidth*positionOffset + position*TabWidth
                scroll(position, positionOffset);

                if(mListener!=null){
                    mListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mListener!=null){
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mListener!=null){
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });

        mViewPager.setCurrentItem(position);
        highLightTextView(position);
    }

    /**
     * 重置Tab文本的颜色
     */
    private void resetTextViewColor(){
        for(int i = 0;i< getChildCount();i++){
            View view = getChildAt(i);
            if(view instanceof TextView){
                ((TextView)view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 高亮某个Tab的文本
     * @param position
     */
    private void highLightTextView(int position){
        resetTextViewColor();
        View view = getChildAt(position);
        if(view instanceof TextView){
            ((TextView)view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    /**
     * Tab的点击事件
     */
    private void setItemClickEvent(){
        int cCount = getChildCount();
        for(int i = 0;i<cCount;i++){
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
