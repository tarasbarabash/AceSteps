package com.tarasbarabash.acesteps.Database;

import android.provider.BaseColumns;

/**
 * Created by Taras
 * 13-Sep-19, 20:33.
 */
public class WorkoutSessionContract {
    private WorkoutSessionContract () {}

    public static class WorkoutEntry implements BaseColumns {
        public static final String TABLE_NAME = "workouts";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_WALKING = "walking_count";
        public static final String COLUMN_JOGGING = "jogging_count";
        public static final String COLUMN_RUNNING = "running_count";
        public static final String COLUMN_AVERAGE_SPEEDS = "avg_speeds";
    }
}
