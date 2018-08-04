package com.example.kylerfcristin.snapface;

//import android.support.design.widget.TabLayout;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kylerfcristin.snapface.adapter.MainPagerAdapter;
import com.example.kylerfcristin.snapface.fragment.EmptyFragment;
import com.example.kylerfcristin.snapface.view.SnapBrowseView;
import com.example.kylerfcristin.snapface.view.SnapTabsView;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;



public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final android.view.View background = findViewById(R.id.am_background_view);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.am_view_pager);
        final MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter); // Every view pager must have adapter'

        SnapBrowseView snapBrowseView = (SnapBrowseView) findViewById(R.id.am_snap_browse);
        snapBrowseView.setUpWithViewPager_Browse(viewPager);

        SnapTabsView snapTabsView = (SnapTabsView) findViewById(R.id.am_snap_tabs);
        snapTabsView.setUpWithViewPager(viewPager);

        snapTabsView.setOnTakePictureListener(new SnapTabsView.TakePictureListener() {
            @Override
            public void onTakePicture() {
                // https://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager
                FragmentPagerAdapter fragmentPagerAdapter = (FragmentPagerAdapter) viewPager
                        .getAdapter();
                EmptyFragment emptyFragment = (EmptyFragment) fragmentPagerAdapter.getItem(1);
                emptyFragment.takePhoto();
            }
        });

        viewPager.setCurrentItem(1);

        final int colorBlue = ContextCompat.getColor(this, R.color.light_blue);
        final int colorPurple = ContextCompat.getColor(this, R.color.light_purple);

        // For "tabs" not used in Snapchat
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.am_tab_layout);
//        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0) {
                    background.setBackgroundColor(colorBlue);
                    background.setAlpha(1 - positionOffset); // Color fades in
                }
                else if(position == 1) {
                    background.setBackgroundColor(colorPurple);
                    background.setAlpha(positionOffset); // Color we want
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}

//public static Camera getCameraInstance() {
//    Camera c = null;
//    try {
//        c = Camera.open();
//    }
//    catch (Exception e){
//
//    }
//    return c;
//}

//public class CameraActivity extends Activity {
//
//    private Camera mCamera;
//    private CameraPreview mPreview;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.view_snap_tabs);
//
//        mCamera = getCameraInstance();
//
//        mPreview = new CameraPreview(this, mCamera);
//        FrameLayout preview = findViewById(R.id.camera_preview);
//        preview.addView(mPreview);
//    }
//}
