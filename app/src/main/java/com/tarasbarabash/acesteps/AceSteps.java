package com.tarasbarabash.acesteps;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.tarasbarabash.acesteps.Services.StepDetector;

/**
 * Created by Taras
 * 14-Sep-19, 16:15.
 */
public class AceSteps extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Intent intent = new Intent(this, StepDetector.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else
            startService(intent);
    }
}
