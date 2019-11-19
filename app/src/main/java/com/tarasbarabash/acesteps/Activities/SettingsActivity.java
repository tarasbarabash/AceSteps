package com.tarasbarabash.acesteps.Activities;

import android.os.Bundle;

import com.tarasbarabash.acesteps.Fragments.SettingsFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Taras
 * 29-Sep-19, 16:42.
 */
public class SettingsActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
    }

    @Override
    public Fragment getFragment() {
        return new SettingsFragment();
    }
}
