package com.tarasbarabash.acesteps.Adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tarasbarabash.acesteps.Constants.BundleKeys;
import com.tarasbarabash.acesteps.Fragments.DayFragment;
import com.tarasbarabash.acesteps.models.Day;

import java.util.ArrayList;

/**
 * Created by Taras
 * 15-Sep-19, 21:39.
 */
public abstract class DaysPagerAdapter extends BasePagerAdapter {
    private static final String TAG = DaysPagerAdapter.class.getSimpleName();
    private ArrayList<Day> mDays;

    public DaysPagerAdapter(FragmentManager fm) {
        super(fm);
        mDays = getDays();
    }

    @Override
    public int getFragmentCount() {
        return mDays.size() ;
    }

    @Override
    public Fragment getFragment(int i) {
        Fragment dayFragment = new DayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BundleKeys.INDEX_KEY, i);
        dayFragment.setArguments(bundle);
        return dayFragment;
    }

    public abstract ArrayList<Day> getDays();
}
