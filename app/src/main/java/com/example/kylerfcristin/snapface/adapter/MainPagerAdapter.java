package com.example.kylerfcristin.snapface.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kylerfcristin.snapface.fragment.ChatFragment;
import com.example.kylerfcristin.snapface.fragment.EmptyFragment;
import com.example.kylerfcristin.snapface.fragment.StoryFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChatFragment.create();
            case 1:
                return EmptyFragment.create();
            case 2:
                return StoryFragment.create();
        }

        return null;
    }


    // For "tab" feature not used in Snapchat
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Chat";
            case 1:
                return "Search";
            case 2:
                return "Stories";
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 3; // For Three pages
    }
}
