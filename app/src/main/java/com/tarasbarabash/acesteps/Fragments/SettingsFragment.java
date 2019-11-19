package com.tarasbarabash.acesteps.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.tarasbarabash.acesteps.Constants.SharedPreferencesNames;
import com.tarasbarabash.acesteps.R;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

/**
 * Created by Taras
 * 29-Sep-19, 16:49.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(SharedPreferencesNames.MAIN_PREFERENCES);
        addPreferencesFromResource(R.xml.settings);
    }
}
