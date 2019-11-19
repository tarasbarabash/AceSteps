package com.tarasbarabash.acesteps.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Taras
 * 15-Sep-19, 20:50.
 */
public class Day extends WorkoutSession implements Serializable {
    private int mDay;
    private int mMonth;
    private int mYear;
    private long duration;

    public Day(int day, int month, int year) {
        super();
        mDay = day;
        mMonth = month;
        mYear = year;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getYear() {
        return mYear;
    }

    public static Day fromCalendar(Calendar calendar) {
        return new Day(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1,
                       calendar.get(Calendar.YEAR)
        );
    }

    @NonNull
    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth - 1, mDay);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public Day setWalking(int walkingCount) {
        super.setWalking(walkingCount);
        return this;
    }

    public Day setJogging(int joggingCount) {
        super.setJogging(joggingCount);
        return this;
    }

    public Day setRunning(int runningCount) {
        super.setRunning(runningCount);
        return this;
    }

    public Day setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public int getMinsDuration() {
        return (int) (duration / 1000 / 60);
    }
}
