package com.example.kylerfcristin.snapface.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.kylerfcristin.snapface.R;
import com.example.kylerfcristin.snapface.Singleton.CameraModule;

public class SnapBrowseView extends FrameLayout implements ViewPager.OnPageChangeListener {

    boolean lightIsOn = false;

    private ImageView mProfilePic;
    private ImageView mCameraFlip;
    private ImageView mSearch;
    private ImageView mLightSwitch;

    private ArgbEvaluator mArgbEvaluator;

    private View mIndicator;

    private int mCenterColor;
    private int mSideColor;


    private int mEndViewsTranslationX;
    private int getmEndViewsTranslationY;
    private int mIndicatorTranslationX;
    private int mLeftViewTranslationX;
    private int mRightViewTranslationX;
    private int mCenterTranslationY;

    public SnapBrowseView(@NonNull Context context) {
        this(context, null);
    }

    public SnapBrowseView(@NonNull Context context, @NonNull AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapBrowseView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAtttr) {
        super(context, attrs, defStyleAtttr);

        browseInit();
    }

    private void browseInit() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_snap_browse, this, true);

        mProfilePic = (ImageView) findViewById(R.id.profile_pic);
        mSearch = (ImageView) findViewById(R.id.magnify_search);
        mLightSwitch = (ImageView) findViewById(R.id.let_there_be_light);
        mCameraFlip = (ImageView) findViewById(R.id.flip_camera);

        mIndicator = findViewById(R.id.vsb_indicator);

        mCenterColor = ContextCompat.getColor(getContext(), R.color.white);
        mSideColor = ContextCompat.getColor(getContext(), R.color.transparent);
        mArgbEvaluator = new ArgbEvaluator();

        mLightSwitch.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEndViewsTranslationX = (int) ((mLightSwitch.getX() - mSearch.getX()));
                mLightSwitch.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mCameraFlip.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEndViewsTranslationX = (int) ((mLightSwitch.getX() - mSearch.getX()));
                mCameraFlip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setUpWithViewPager_Browse(final ViewPager viewPager_browse) {
        viewPager_browse.addOnPageChangeListener(this);

        mLightSwitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!lightIsOn) {
                            mLightSwitch.setImageResource(R.drawable.ic_flash_on_24dp);
                            lightIsOn = true;
                        }
                        else {
                            mLightSwitch.setImageResource(R.drawable.ic_flash_off_24dp);
                            lightIsOn = false;
                            }
                    }
                }
        );
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            setColor(1 - positionOffset);
            moveViews(1 - positionOffset);

            mIndicator.setTranslationX((positionOffset - 1) * mIndicatorTranslationX);
        } else if (position == 1) {
            setColor(positionOffset);
            moveViews(positionOffset);

            mIndicator.setTranslationX(positionOffset * mIndicatorTranslationX);
        }
    }

    public void onPageSelected(int position) {
        Log.d("SnapBrowseView", "on page selected " + position);
    }


    public void onPageScrollStateChanged(int state) {

    }

    private void moveViews(float fractionFromCenter) {
        mCameraFlip.setTranslationX(fractionFromCenter * mEndViewsTranslationX);
        mLightSwitch.setTranslationX(fractionFromCenter * mEndViewsTranslationX);
        mIndicator.setTranslationX(fractionFromCenter * mEndViewsTranslationX);
    }

    private void setColor(float fractionFromCenter) {
        int color = (int) mArgbEvaluator.evaluate(fractionFromCenter, mCenterColor, mSideColor);

        mCameraFlip.setColorFilter(color);
        mLightSwitch.setColorFilter(color);

    }


}
