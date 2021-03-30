package com.example.pi.mqtt_test;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    SectionsPageAdapter(FragmentManager fm) { super(fm); }

    void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) { return mFragmentTitleList.get(position); }

    @Override
    public Fragment getItem(int position) { return mFragmentList.get(position); }

    @Override
    public int getItemPosition(Object object) { return PagerAdapter.POSITION_NONE; }

    @Override
    public int getCount() { return mFragmentList.size(); }
}
