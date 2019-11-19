package com.tarasbarabash.acesteps.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.tarasbarabash.acesteps.Activities.MainActivity;
import com.tarasbarabash.acesteps.Constants.ChannelsIds;
import com.tarasbarabash.acesteps.Constants.SharedPreferencesNames;
import com.tarasbarabash.acesteps.Constants.TasksConstants;
import com.tarasbarabash.acesteps.Listeners.AccelerometerListener;
import com.tarasbarabash.acesteps.Listeners.AccelerometerListener.AccelerometerListenerInterface;
import com.tarasbarabash.acesteps.R;
import com.tarasbarabash.acesteps.Utilities.SharedPrefsUtils;
import com.tarasbarabash.acesteps.models.AccelerometerData;
import com.tarasbarabash.acesteps.models.CustomNotification;
import com.tarasbarabash.acesteps.models.WorkoutSession;
import com.tarasbarabash.acesteps.Tasks.ProcessAccelerometerDataTask;
import com.tarasbarabash.acesteps.Tasks.ProcessAccelerometerDataTask.StepDetectorInterface;
import com.tarasbarabash.acesteps.Tasks.WorkoutFinishedTask;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import static com.tarasbarabash.acesteps.Tasks.WorkoutFinishedTask.*;

public class StepDetector extends Service
        implements AccelerometerListenerInterface, StepDetectorInterface, WorkoutFinishedListener {

    private static final String TAG = StepDetector.class.getName();
    private ScheduledExecutorService mExecutorService;
    private ArrayList<AccelerometerData> mRawData = new ArrayList<>();
    private WorkoutSession mSession;
    private ScheduledFuture mSavingScheduled;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CustomNotification notification = new CustomNotification(
                    this,
                    ChannelsIds.BACKGROUND_CHANNEL,
                    this.getString(R.string.service_notification_channel_name),
                    this.getString(R.string.service_notification_channel_description),
                    NotificationManager.IMPORTANCE_MIN,
                    this.getString(R.string.service_notification_title),
                    null,
                    R.drawable.ic_directions_walk_black_24dp,
                    NotificationCompat.PRIORITY_MIN
            );
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                                                                    PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder builder = notification.getBuilder();
            builder.setContentIntent(pendingIntent);
            startForeground(101, builder.build());
        }
        Log.i(TAG, "Step detector service has started!");
        mExecutorService = Executors.newScheduledThreadPool(2);
        SensorManager sensorManager =
                (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) Log.e(TAG, "Accelerometer is not found!");
        AccelerometerListener accelerometerListener = new AccelerometerListener(this);
        sensorManager.registerListener(accelerometerListener, accelerometer,
                                       SensorManager.SENSOR_DELAY_FASTEST
        );
        ProcessAccelerometerDataTask processorTask = new ProcessAccelerometerDataTask(
                mRawData,
                this
        );
        mExecutorService.scheduleWithFixedDelay(processorTask, TasksConstants.PROCESSOR_DELAY,
                                                TasksConstants.PROCESSOR_DELAY,
                                                TimeUnit.MILLISECONDS);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onChange(AccelerometerData data) {
        mRawData.add(data);
    }

    @Override
    public void onStepDetected(ArrayList<AccelerometerData> accelerometerData) {
        Log.e(TAG, "onStepDetected: will save in: " + (mSavingScheduled != null ? mSavingScheduled.getDelay(TimeUnit.MILLISECONDS) : 0));
        Log.e(TAG, "onStepDetected: Session: "  + (mSession == null ));
        int height = SharedPrefsUtils.getIntStoredAsString(this, SharedPreferencesNames.HEIGHT, 176, null);
        if (accelerometerData.size() < 7) {
            Log.wtf(TAG, "No steps detected");
            if (mSession != null)
                mSession.addSpeedToArray(0);
            return;
        }
        WorkoutSession localSession = new WorkoutSession();
        localSession.setStartTime(accelerometerData.get(0).getTime());
        Log.e(TAG, "onStepDetected: Session: "  + (mSession == null ));
        if (mSession == null) {
            mSession = new WorkoutSession();
            long firstStepTime = accelerometerData.get(0).getTime();
            mSession.setStartTime(firstStepTime);
        }
        for (AccelerometerData data : accelerometerData) {
            mSession.addStep(data.getValue());
            localSession.addStep(data.getValue());
        }
        long lastStepTime = accelerometerData.get(accelerometerData.size() - 1).getTime();
        mSession.setEndTime(lastStepTime);
        localSession.setEndTime(lastStepTime);
        Log.e(TAG, "onStepDetected: " + mSession.getTotalSteps());
        mSession.addSpeedToArray(localSession.getAverageSpeed(
                localSession.getLengthInMillis(),
                localSession.getDistance(height)
        ));
        if (mSavingScheduled != null) mSavingScheduled.cancel(true);
        Log.e(TAG, "onStepDetected: will save in: " + (mSavingScheduled != null ? mSavingScheduled.getDelay(TimeUnit.MILLISECONDS) : 0));
        mSavingScheduled = mExecutorService.schedule(
                new WorkoutFinishedTask(
                        this,
                        mSession,
                        this
                ),
                TasksConstants.SAVER_DELAY,
                TimeUnit.MILLISECONDS
        );

        Log.e(TAG, "onStepDetected: will save in: " + (mSavingScheduled != null ? mSavingScheduled.getDelay(TimeUnit.MILLISECONDS) : 0));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
    }

    @Override
    public void onSavingFinished() {
        mSession = null;
        mSavingScheduled = null;
    }
}
