package com.tarasbarabash.acesteps.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by Taras
 * 15-Sep-19, 20:41.
 */
public abstract class BasePagerAdapter extends FragmentStatePagerAdapter {
    public BasePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return getFragment(i);
    }

    @Override
    public int getCount() {
        return getFragmentCount();
    }

    public abstract int getFragmentCount();
    public abstract Fragment getFragment(int i);
}
