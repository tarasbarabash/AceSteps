package com.tarasbarabash.acesteps.Database;

import android.provider.BaseColumns;

/**
 * Created by Taras
 * 15-Sep-19, 13:52.
 */
public final class StepsContract {
    private StepsContract() {}
    
    public final class StepsEntry implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_WALKING = WorkoutSessionContract.WorkoutEntry.COLUMN_WALKING;
        public static final String COLUMN_JOGGING = WorkoutSessionContract.WorkoutEntry.COLUMN_JOGGING;
        public static final String COLUMN_RUNNING = WorkoutSessionContract.WorkoutEntry.COLUMN_RUNNING;
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_TIME_REACHED = "target_time_reached";
        public static final String COLUMN_STEPS_REACHED = "target_steps_reached";
    }
}
