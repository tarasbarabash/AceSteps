package com.tarasbarabash.acesteps.viewModels;

import androidx.databinding.Bindable;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tarasbarabash.acesteps.Adapters.WorkoutsAdapter;
import com.tarasbarabash.acesteps.Constants.BundleKeys;
import com.tarasbarabash.acesteps.Constants.ChannelsIds;
import com.tarasbarabash.acesteps.Constants.SharedPreferencesNames;
import com.tarasbarabash.acesteps.Database.StepsContract;
import com.tarasbarabash.acesteps.Database.WorkoutDBHelper;
import com.tarasbarabash.acesteps.Fragments.BaseFragment;
import com.tarasbarabash.acesteps.R;
import com.tarasbarabash.acesteps.Utilities.SharedPrefsUtils;
import com.tarasbarabash.acesteps.databinding.FragmentDayBinding;
import com.tarasbarabash.acesteps.models.CustomNotification;
import com.tarasbarabash.acesteps.models.Day;
import com.tarasbarabash.acesteps.models.WorkoutSession;

import java.util.ArrayList;
import java.util.Calendar;

import static com.tarasbarabash.acesteps.Constants.SharedPreferencesNames.TARGET_ACTIVITY_MINS_KEY;
import static com.tarasbarabash.acesteps.Constants.SharedPreferencesNames.TARGET_STEPS_KEY;

/**
 * Created by Taras
 * 15-Sep-19, 21:15.
 */
public class DayVM extends FragmentVM<FragmentDayBinding> implements WorkoutDBHelper.StepsListener {
    private static final String TAG = DayVM.class.getSimpleName();
    private Day mDay;
    private int mIndex;
    private ArrayList<WorkoutSession> mWorkouts;
    private WorkoutDBHelper mDBHelper;
    private WorkoutsAdapter mAdapter;
    private float mProgress;

    public DayVM(FragmentDayBinding binding, BaseFragment fragment) {
        super(binding, fragment);
        Bundle bundle = fragment.getArguments();
        if (bundle == null) return;
        mIndex = bundle.getInt(BundleKeys.INDEX_KEY);
        boolean isToday = mIndex == 0;
        mDBHelper = WorkoutDBHelper.getInstance(getContext());
        if (isToday) mDBHelper.setListener(this);
        mDay = mDBHelper.getDays().get(mIndex);
        loadWorkouts();
    }

    private void loadWorkouts() {
        long[] startEndTimestamps = getStartEndTimestamps();
        mWorkouts = mDBHelper.getWorkouts(
                startEndTimestamps[0],
                startEndTimestamps[1]
        );
        RecyclerView recyclerView = getBinding().recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new WorkoutsAdapter(getContext(), mWorkouts, recyclerView);
        recyclerView.setAdapter(mAdapter);
    }

    private long[] getStartEndTimestamps() {
        Calendar c = Calendar.getInstance();
        c.set(mDay.getYear(), mDay.getMonth() - 1, mDay.getDay(), 0, 0, 0);
        long start = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, 1);
        long end = c.getTimeInMillis();
        return new long[]{start, end};
    }

    @Bindable
    public Day getDay() {
        return mDay;
    }

    public float getDistance() {
        int height = SharedPrefsUtils.getIntStoredAsString(getContext(), SharedPreferencesNames.HEIGHT, 176, null);
        return mDay.getDistance(height);
    }

    @Bindable
    public int getProgress() {
        int target = SharedPrefsUtils.getIntStoredAsString(getContext(), TARGET_STEPS_KEY, 6000, null);
        mProgress = (mDay.getTotalSteps() / (float) target * 100f);
        return mProgress > 100 ? 100 : (int) Math.floor(mProgress);
    }

    public int getTargetSteps() {
        return SharedPrefsUtils.getIntStoredAsString(getContext(), TARGET_STEPS_KEY, 6000, null);
    }

    public boolean getTimeTargetReached() {
        int mins = SharedPrefsUtils.getIntStoredAsString(getContext(), TARGET_ACTIVITY_MINS_KEY, 60, null);
        return mins <= mDay.getMinsDuration();
    }

    @Bindable
    public ArrayList<WorkoutSession> getWorkouts() {
        return mWorkouts;
    }

    @Override
    public void onStepsInserted() {
        int targetSteps = getTargetSteps();
        int targetActivity = SharedPrefsUtils.getIntStoredAsString(
                getContext(),
                TARGET_ACTIVITY_MINS_KEY,
                60,
                null
        );
        mDay = mDBHelper.getDays().get(mIndex);
        notifyPropertyChanged(com.tarasbarabash.acesteps.BR.day);
        notifyPropertyChanged(com.tarasbarabash.acesteps.BR.progress);
        if (mDay.getMinsDuration() > targetActivity) {
            boolean targetActivityReached =
                    mDBHelper.isTargetReached(StepsContract.StepsEntry.COLUMN_TIME_REACHED, mDay);
            if (!targetActivityReached) {
                mDBHelper.setTargetReached(StepsContract.StepsEntry.COLUMN_TIME_REACHED, mDay);
                CustomNotification customNotification = new CustomNotification(
                        getContext(),
                        ChannelsIds.TARGET_CHANNEL,
                        getContext().getString(R.string.target_reached_notification_channel),
                        getContext().getString(R.string.target_reached_notification_channel_description),
                        3,
                        getContext().getString(R.string.target_notification_title),
                        getContext().getString(R.string.target_activity_time_reached_description),
                        R.drawable.cup,
                        NotificationCompat.PRIORITY_DEFAULT
                );
                customNotification.sendNotification(3);
            }
        }
        if (mDay.getTotalSteps() > targetSteps) {
            boolean targetActivityReached =
                    mDBHelper.isTargetReached(StepsContract.StepsEntry.COLUMN_STEPS_REACHED, mDay);
            if (!targetActivityReached) {
                mDBHelper.setTargetReached(StepsContract.StepsEntry.COLUMN_STEPS_REACHED, mDay);
                CustomNotification customNotification = new CustomNotification(
                        getContext(),
                        ChannelsIds.TARGET_CHANNEL,
                        getContext().getString(R.string.target_reached_notification_channel),
                        getContext().getString(R.string.target_reached_notification_channel_description),
                        3,
                        getContext().getString(R.string.target_notification_title),
                        getContext().getString(R.string.target_steps_reached_description),
                        R.drawable.cup,
                        NotificationCompat.PRIORITY_DEFAULT
                );
                customNotification.sendNotification(4);
            }
        }
    }

    @Override
    public void onNewWorkout(String id) {
        long[] startEndTimestamps = getStartEndTimestamps();
        mWorkouts = mDBHelper.getWorkouts(
                startEndTimestamps[0],
                startEndTimestamps[1]
        );
        notifyPropertyChanged(com.tarasbarabash.acesteps.BR.workouts);
        WorkoutSession workoutSession = mDBHelper.getWorkoutById(id);
        getActivity().runOnUiThread(() -> mAdapter.addItem(workoutSession)
        );
    }

    @Override
    public void onWorkoutRemoved(String id) {
        long[] startEndTimestamps = getStartEndTimestamps();
        mWorkouts = mDBHelper.getWorkouts(
                startEndTimestamps[0],
                startEndTimestamps[1]
        );
        notifyPropertyChanged(com.tarasbarabash.acesteps.BR.workouts);
        getActivity().runOnUiThread(() -> mAdapter.removeItem(id)
        );
    }
}
