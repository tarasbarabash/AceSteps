package com.tarasbarabash.acesteps.Activities;

import androidx.fragment.app.Fragment;

import com.tarasbarabash.acesteps.Fragments.WorkoutFragment;

/**
 * Created by Taras
 * 20-Sep-19, 18:28.
 */
public class WorkoutActivity extends BaseFragmentActivity {
    private static final String TAG = WorkoutActivity.class.getSimpleName();

    @Override
    public Fragment getFragment() {
        return new WorkoutFragment();
    }
}
