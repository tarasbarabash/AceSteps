package com.tarasbarabash.acesteps.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.tarasbarabash.acesteps.Constants.SharedPreferencesNames;

/**
 * Created by Taras
 * 19-Sep-19, 19:31.
 */
public class SharedPrefsUtils {
    public static final String DEFAULT_SP_NAME = SharedPreferencesNames.MAIN_PREFERENCES;

    public static int getInt(Context context, String key, int defaultValue, String preferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName == null ? DEFAULT_SP_NAME : preferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static int getIntStoredAsString(Context context, String key, int defaultValue, String preferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName == null ? DEFAULT_SP_NAME : preferencesName, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value == null ? defaultValue : Integer.parseInt(value);
    }
}
