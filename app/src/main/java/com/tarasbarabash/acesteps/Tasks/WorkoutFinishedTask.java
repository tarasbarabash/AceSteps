package com.tarasbarabash.acesteps.Tasks;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.tarasbarabash.acesteps.Activities.MainActivity;
import com.tarasbarabash.acesteps.Activities.WorkoutActivity;
import com.tarasbarabash.acesteps.Constants.BundleKeys;
import com.tarasbarabash.acesteps.Constants.ChannelsIds;
import com.tarasbarabash.acesteps.Database.WorkoutDBHelper;
import com.tarasbarabash.acesteps.models.CustomNotification;
import com.tarasbarabash.acesteps.models.WorkoutSession;
import com.tarasbarabash.acesteps.R;

/**
 * Created by Taras
 * 14-Sep-19, 14:52.
 */
public class WorkoutFinishedTask implements Runnable {
    private static final String TAG = WorkoutFinishedTask.class.getSimpleName();
    private Context mContext;
    private WorkoutSession mSession;
    private WorkoutDBHelper mDB;
    private WorkoutFinishedListener mListener;

    public interface WorkoutFinishedListener {
        void onSavingFinished();
    }

    public WorkoutFinishedTask(Context context, WorkoutSession session,
                               WorkoutFinishedListener listener) {
        mContext = context;
        mSession = session;
        mListener = listener;
        mDB = WorkoutDBHelper.getInstance(mContext);
    }

    @Override
    @SuppressLint("NewApi")
    public void run() {
        Log.e(TAG, "Saving workout: " + mSession.toString());
        try {
            if (mSession.getTotalSteps() < 5) return;
            if (mSession.getLengthInMinutes() < 5)
                throw new Exception("Skipping the workout! Total length: " + mSession
                        .getLengthInMinutes() +
                                            " minutes, total steps: " + mSession.getTotalSteps());
            if (!saveToDb()) {
                Log.e(TAG, "Saving failed!");
                return;
            }
            CustomNotification customNotification =
                    new CustomNotification(mContext, ChannelsIds.WORKOUT_CHANNEL,
                                           mContext.getString(R.string.workout_notifications_channel_title),
                                           mContext.getString(R.string.workout_notification_channel_description),
                                           NotificationManager.IMPORTANCE_DEFAULT,
                                           mContext.getString(R.string.workout_detected_notification_title),
                                           mContext.getString(
                                                   R.string.workout_notification_title,
                                                   mSession.getLengthInMinutes(),
                                                   mContext.getString(mSession.getWorkoutTitle())
                                                           .toLowerCase()
                                           ),
                                           R.drawable.ic_directions_walk_black_24dp,
                                           NotificationCompat.PRIORITY_DEFAULT
                    );
            Intent backIntent = new Intent(mContext, MainActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = new Intent(mContext, WorkoutActivity.class);
            intent.putExtra(BundleKeys.WORKOUT_ID_NOTIFICATION, mSession.getId().toString());
            PendingIntent pendingIntent = PendingIntent.getActivities(mContext, 0,
                                                                      new Intent[]{backIntent,
                                                                              intent},
                                                                      PendingIntent.FLAG_ONE_SHOT
            );
            customNotification.setIntent(pendingIntent);
            customNotification.setAutoCancel();
            customNotification.sendNotification(2);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            Log.e(TAG, "run: " + mSession.getLengthInMillis());
            boolean isSaved = mDB.saveSteps(mSession);
            Log.i(TAG, isSaved ? "Steps were saved!" : "Steps were not saved!");
            mListener.onSavingFinished();
        }
    }

    private boolean saveToDb() {
        boolean isSaved = mDB.saveWorkout(mSession);
        Log.i(TAG, isSaved ? "Saved!" : "Not saved!");
        return isSaved;
    }
}
