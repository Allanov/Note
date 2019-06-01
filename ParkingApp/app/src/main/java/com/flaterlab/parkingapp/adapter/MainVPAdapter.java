package com.flaterlab.parkingapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.flaterlab.parkingapp.screen.fragments.MapFragment;

public class MainVPAdapter extends FragmentStatePagerAdapter {
    public MainVPAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new Fragment();
            default:
                throw new IllegalArgumentException("No such fragment");

        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
