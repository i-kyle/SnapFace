package com.example.kylerfcristin.snapface.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.kylerfcristin.snapface.R;

public class SnapTabsView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ImageView mCenterImage;
    private ImageView mChatImage;
    private ImageView mStoriesImage;
    private ImageView mBottomImage;

    private View mIndicator;

    private ArgbEvaluator mArgbEvaluator;

    private int mCenterColor;
    private int mSideColor;

    private int mEndViewsTranslationX;
    private int mIndicatorTranslationX;
    private int mCenterTranslationY;

    public SnapTabsView(@NonNull Context context) {
        this(context, null);
    }

    public SnapTabsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapTabsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setUpWithViewPager(final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(this);

        mChatImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(0);
            }
        });

        mStoriesImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem() != 2)
                    viewPager.setCurrentItem(2);
            }
        });

    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_snap_tabs, this, true);

        mCenterImage = (ImageView) findViewById(R.id.vst_center_image);
        mChatImage = (ImageView) findViewById(R.id.vst_chat_image);
        mStoriesImage = (ImageView) findViewById(R.id.vst_stories_image);
        mBottomImage = (ImageView) findViewById(R.id.vst_bottom_image);

        mIndicator = findViewById(R.id.vst_indicator);

        mCenterColor = ContextCompat.getColor(getContext(), R.color.white);
        mSideColor = ContextCompat.getColor(getContext(), R.color.dark_grey);

        mArgbEvaluator = new ArgbEvaluator();

        mIndicatorTranslationX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

        mBottomImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEndViewsTranslationX = (int) ((mBottomImage.getX() - mChatImage.getX()) - mIndicatorTranslationX);
                mBottomImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mCenterTranslationY = getHeight() - mBottomImage.getBottom();

            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position == 0) {
            setColor(1 - positionOffset);
            moveViews(1 - positionOffset);

            moveAndScaleCenter(1 - positionOffset);

            mIndicator.setTranslationX((positionOffset - 1) * mIndicatorTranslationX);
        }
        else if(position == 1) {
            setColor(positionOffset);
            moveViews(positionOffset);

            moveAndScaleCenter(positionOffset);

            mIndicator.setTranslationX(positionOffset * mIndicatorTranslationX);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void moveAndScaleCenter(float fractionFromCenter) {
        float scale = .7f + ((1 - fractionFromCenter) * .3f);

        mCenterImage.setScaleX(scale);
        mCenterImage.setScaleY(scale);

        int translation = (int) (fractionFromCenter * mCenterTranslationY);

        mCenterImage.setTranslationY(translation);
        mBottomImage.setTranslationY(translation);

        mBottomImage.setAlpha(1 - fractionFromCenter);
    }

    private void moveViews(float fractionFromCenter) {
        mChatImage.setTranslationX(fractionFromCenter * mEndViewsTranslationX);
        mStoriesImage.setTranslationX(-fractionFromCenter * mEndViewsTranslationX);

        mIndicator.setAlpha(fractionFromCenter);
        mIndicator.setScaleX(fractionFromCenter);
    }

    private void setColor(float fractionFromCenter) {
        int color = (int) mArgbEvaluator.evaluate(fractionFromCenter, mCenterColor, mSideColor);

        mCenterImage.setColorFilter(color);
        mChatImage.setColorFilter(color);
        mStoriesImage.setColorFilter(color);
    }
}
