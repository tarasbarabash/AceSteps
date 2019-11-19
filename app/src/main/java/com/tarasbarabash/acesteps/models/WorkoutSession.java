package com.tarasbarabash.acesteps.models;

import androidx.annotation.Nullable;

import com.tarasbarabash.acesteps.Constants.StepsTypes;
import com.tarasbarabash.acesteps.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class WorkoutSession {
    private static final String TAG = WorkoutSession.class.getSimpleName();
    private UUID mId;
    private long mStartTime;
    private long mEndTime;
    private int mWalkingCount = 0;
    private int mJoggingCount = 0;
    private int mRunningCount = 0;
    private ArrayList<Float> mAvgSpeed;

    public WorkoutSession() {
        mId = UUID.randomUUID();
        mAvgSpeed = new ArrayList<>();
    }

    public WorkoutSession(UUID id) {
        mId = id;
        mAvgSpeed = new ArrayList<>();
    }

    public WorkoutSession(UUID id,
                          long startTime,
                          long endTime,
                          int walkingCount,
                          int joggingCount,
                          int runningCount,
                          ArrayList<Float> avgSpeed) {
        this.mId = id;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        mWalkingCount = walkingCount;
        mJoggingCount = joggingCount;
        mRunningCount = runningCount;
        mAvgSpeed = avgSpeed;
    }

    public long getLengthInMillis() {
        return this.mEndTime - this.mStartTime;
    }

    public int getLengthInMinutes() {
        return (int) (getLengthInMillis() / 1000f / 60f);
    }

    public int getTotalSteps() {
        return mWalkingCount + mJoggingCount + mRunningCount;
    }

    public UUID getId() {
        return mId;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public void addStep(double value) {
        if (value >= StepsTypes.WALKING && value <= StepsTypes.JOGGING) mWalkingCount++;
        else if (value >= StepsTypes.JOGGING && value <= StepsTypes.RUNNING) mJoggingCount++;
        else if (value >= StepsTypes.RUNNING) mRunningCount++;
    }

    public String getTimeString(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        date.setTime(time);
        return dateFormat.format(date);
    }

    public int getDurationSeconds (long duration) {
        return (int) (duration / 1000) % 60;
    }

    public int getDurationMinutes(long duration) {
        return (int) ((duration / (1000 * 60)) % 60);
    }

    public int getDurationHours(long duration) {
        return (int) ((duration / (1000 * 60 * 60)) % 24);
    }

    public String getDurationOnlyMinsAndSecs(long duration) {
        int seconds = getDurationSeconds(duration);
        int minutes = getDurationMinutes(duration);
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    public String getDurationString(long duration) {
        int seconds = getDurationSeconds(duration);
        int minutes = getDurationMinutes(duration);
        int hours = getDurationHours(duration);
        return String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
        );
    }

    public float getDistance(int height) {
        return getTotalSteps() * (height * 0.46f / 100000f);
    }

    public int getWorkoutTitle() {
        ArrayList<Integer> steps = new ArrayList<>();
        steps.add(mWalkingCount);
        steps.add(mJoggingCount);
        steps.add(mRunningCount);
        ArrayList<Integer> sorted = new ArrayList<>(steps);
        Collections.sort(sorted);
        int index = steps.indexOf(sorted.get(sorted.size() - 1));
        switch (index) {
            case 0:
                return R.string.walking_title;
            case 1:
                return (R.string.jogging_title);
            case 2:
                return (R.string.running_title);
        }
        return R.string.running_title;
    }

    public int getWalkingCount() {
        return mWalkingCount;
    }

    public int getJoggingCount() {
        return mJoggingCount;
    }

    public int getRunningCount() {
        return mRunningCount;
    }

    public int getCaloriesBurnt() {
        return (int) (getTotalSteps() * 0.2063337f );
    }

    public float getAverageSpeed(long duration, float distance) {
        float speed = distance / (duration / 1000f / 60 / 60);
        return Float.isNaN(speed) || Float.isInfinite(speed) ? 0 : speed;
    }

    public String getAveragePace(long duration, float distance) {
        return getDurationOnlyMinsAndSecs((long) (duration / distance));
    }

    @Override
    public String toString() {
        return this.mStartTime + "; " + this.mEndTime + ": walking: " + this.mWalkingCount + ", " +
                "jogging: " + mJoggingCount + ", running: " + this.mRunningCount;
    }

    public WorkoutSession setWalking(int walkingCount) {
        mWalkingCount = walkingCount;
        return this;
    }

    public WorkoutSession setJogging(int joggingCount) {
        mJoggingCount = joggingCount;
        return this;
    }

    public WorkoutSession setRunning(int runningCount) {
        mRunningCount = runningCount;
        return this;
    }

    public WorkoutSession setStartTime(long startTime) {
        mStartTime = startTime;
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof WorkoutSession)) return false;
        return ((WorkoutSession) obj).getId() == this.mId;
    }

    public void addSpeedToArray(float averageSpeed) {
        mAvgSpeed.add(averageSpeed);
    }

    public ArrayList<Float> getAvgSpeed() {
        return mAvgSpeed;
    }
}
