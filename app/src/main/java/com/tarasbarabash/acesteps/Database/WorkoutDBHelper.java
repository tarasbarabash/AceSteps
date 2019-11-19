package com.tarasbarabash.acesteps.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tarasbarabash.acesteps.models.Day;
import com.tarasbarabash.acesteps.models.WorkoutSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Taras
 * 13-Sep-19, 20:40.
 */
public class WorkoutDBHelper extends SQLiteOpenHelper {
    private static final String TAG = WorkoutDBHelper.class.getSimpleName();

    public interface StepsListener {
        void onStepsInserted();

        void onNewWorkout(String id);
        void onWorkoutRemoved(String id);
    }

    private static final String NAME = "workouts.db";
    private static final int VERSION = 11;
    private StepsListener mListener;
    private static WorkoutDBHelper sInstance;

    public static WorkoutDBHelper getInstance(Context context) {
        if (sInstance == null) sInstance = new WorkoutDBHelper(context);
        return sInstance;
    }

    private WorkoutDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_WORKOUTS_TABLE =
                "CREATE TABLE " + WorkoutSessionContract.WorkoutEntry.TABLE_NAME + "(" +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_ID + " TEXT PRIMARY KEY, " +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_START_TIME + " LONG, " +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_END_TIME + " LONG, " +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_JOGGING + " INTEGER, " +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_RUNNING + " INTEGER, " +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_WALKING + " INTEGER," +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_AVERAGE_SPEEDS + " STRING);";

        final String CREATE_STEPS_TABLE =
                "CREATE TABLE " + StepsContract.StepsEntry.TABLE_NAME + "(" +
                        StepsContract.StepsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        StepsContract.StepsEntry.COLUMN_DAY + " INTEGER, " +
                        StepsContract.StepsEntry.COLUMN_MONTH + " INTEGER, " +
                        StepsContract.StepsEntry.COLUMN_YEAR + " INTEGER, " +
                        StepsContract.StepsEntry.COLUMN_JOGGING + " INTEGER, " +
                        StepsContract.StepsEntry.COLUMN_WALKING + " INTEGER, " +
                        StepsContract.StepsEntry.COLUMN_RUNNING + " INTEGER," +
                        StepsContract.StepsEntry.COLUMN_DURATION + " LONG," +
                        StepsContract.StepsEntry.COLUMN_STEPS_REACHED + " BOOLEAN DEFAULT 0, " +
                        StepsContract.StepsEntry.COLUMN_TIME_REACHED + " BOOLEAN DEFAULT 0);";

        db.execSQL(CREATE_WORKOUTS_TABLE);
        db.execSQL(CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutSessionContract.WorkoutEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StepsContract.StepsEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean saveSteps(WorkoutSession workoutSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        long startTime = workoutSession.getStartTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String where =
                StepsContract.StepsEntry.COLUMN_DAY + "=? AND "
                        + StepsContract.StepsEntry.COLUMN_MONTH + "=? AND "
                        + StepsContract.StepsEntry.COLUMN_YEAR + "=?";
        String[] whereArgs = new String[]{
                String.valueOf(day),
                String.valueOf(month),
                String.valueOf(year)
        };
        Cursor cursor = db.query(
                StepsContract.StepsEntry.TABLE_NAME,
                new String[]{
                        StepsContract.StepsEntry.COLUMN_RUNNING,
                        StepsContract.StepsEntry.COLUMN_WALKING,
                        StepsContract.StepsEntry.COLUMN_JOGGING,
                        StepsContract.StepsEntry.COLUMN_DURATION
                },
                where,
                whereArgs,
                null,
                null,
                null
        );
        int oldRunning = 0, oldWalking = 0, oldJogging = 0;
        long oldDuration = 0;
        boolean exists = cursor.moveToFirst();
        if (exists) {
            oldWalking =
                    cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_WALKING));
            oldJogging =
                    cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_JOGGING));
            oldRunning =
                    cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_RUNNING));
            oldDuration =
                    cursor.getLong(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_DURATION));
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put(
                StepsContract.StepsEntry.COLUMN_WALKING,
                oldWalking + workoutSession.getWalkingCount()
        );
        cv.put(
                StepsContract.StepsEntry.COLUMN_JOGGING,
                oldJogging + workoutSession.getJoggingCount()
        );
        cv.put(
                StepsContract.StepsEntry.COLUMN_RUNNING,
                oldRunning + workoutSession.getRunningCount()
        );
        cv.put(
                StepsContract.StepsEntry.COLUMN_DURATION,
                oldDuration + workoutSession.getLengthInMillis()
        );
        long insert = 0;
        if (!exists) {
            cv.put(StepsContract.StepsEntry.COLUMN_DAY, day);
            cv.put(StepsContract.StepsEntry.COLUMN_MONTH, month);
            cv.put(StepsContract.StepsEntry.COLUMN_YEAR, year);

            insert = db.insert(StepsContract.StepsEntry.TABLE_NAME, null, cv);
        }
        else insert = db.update(StepsContract.StepsEntry.TABLE_NAME, cv, where, whereArgs);
        if (insert > 0 && mListener != null) mListener.onStepsInserted();
        return insert > 0;
    }

    public boolean saveWorkout(WorkoutSession workoutSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(
                WorkoutSessionContract.WorkoutEntry.COLUMN_START_TIME,
                workoutSession.getStartTime()
        );
        cv.put(WorkoutSessionContract.WorkoutEntry.COLUMN_END_TIME, workoutSession.getEndTime());
        cv.put(WorkoutSessionContract.WorkoutEntry.COLUMN_ID, workoutSession.getId().toString());
        cv.put(
                WorkoutSessionContract.WorkoutEntry.COLUMN_JOGGING,
                workoutSession.getJoggingCount()
        );
        cv.put(
                WorkoutSessionContract.WorkoutEntry.COLUMN_RUNNING,
                workoutSession.getRunningCount()
        );
        cv.put(
                WorkoutSessionContract.WorkoutEntry.COLUMN_WALKING,
                workoutSession.getWalkingCount()
        );
        Gson gson = new Gson();
        ArrayList<Float> speeds = workoutSession.getAvgSpeed();
        cv.put(WorkoutSessionContract.WorkoutEntry.COLUMN_AVERAGE_SPEEDS, gson.toJson(speeds));
        long row = db.insert(WorkoutSessionContract.WorkoutEntry.TABLE_NAME, null, cv);
        if (row > 0) mListener.onNewWorkout(workoutSession.getId().toString());
        return row > 0;
    }

    public ArrayList<WorkoutSession> getWorkouts(long startTime, long endTime) {
        ArrayList<WorkoutSession> sessions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection =
                WorkoutSessionContract.WorkoutEntry.COLUMN_START_TIME + " >= ? AND " +
                        WorkoutSessionContract.WorkoutEntry.COLUMN_END_TIME + " <= ?";
        String[] selectionArgs = new String[]{String.valueOf(startTime), String.valueOf(endTime)};
        Cursor cursor = db.query(
                WorkoutSessionContract.WorkoutEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                WorkoutSessionContract.WorkoutEntry.COLUMN_END_TIME + " DESC",
                null
        );
        Gson gson = new Gson();
        while (cursor.moveToNext()) sessions.add(new WorkoutSession(
                UUID.fromString(cursor.getString(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_ID))),
                cursor.getLong(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_START_TIME)),
                cursor.getLong(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_END_TIME)),
                cursor.getInt(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_WALKING)),
                cursor.getInt(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_JOGGING)),
                cursor.getInt(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_RUNNING)),
                gson.fromJson(cursor.getString(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_AVERAGE_SPEEDS)), new TypeToken<ArrayList<Float>>() {
                }.getType())
        ));
        cursor.close();
        return sessions;
    }

    public WorkoutSession getWorkoutById(String workoutId) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = WorkoutSessionContract.WorkoutEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{workoutId};
        Cursor cursor = db.query(
                WorkoutSessionContract.WorkoutEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        WorkoutSession session = null;
        Gson gson = new Gson();
        if (cursor.moveToFirst()) {
            session = new WorkoutSession(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_ID))),
                    cursor.getLong(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_START_TIME)),
                    cursor.getLong(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_WALKING)),
                    cursor.getInt(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_JOGGING)),
                    cursor.getInt(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_RUNNING)),
                    gson.fromJson(cursor.getString(cursor.getColumnIndex(WorkoutSessionContract.WorkoutEntry.COLUMN_AVERAGE_SPEEDS)), new TypeToken<ArrayList<Float>>() {
                    }.getType())
            );
        }
        return session;
    }

    public boolean removeWorkout(String workoutId) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = WorkoutSessionContract.WorkoutEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[] {workoutId};
        int i = db.delete(WorkoutSessionContract.WorkoutEntry.TABLE_NAME, selection,
        selectionArgs);
        if (i > 0) mListener.onWorkoutRemoved(workoutId);
        return i > 0;
    }

    public ArrayList<Day> getDays() {
        ArrayList<Day> days = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                StepsContract.StepsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                StepsContract.StepsEntry.COLUMN_YEAR + " DESC, "
                        + StepsContract.StepsEntry.COLUMN_MONTH + " DESC, "
                        + StepsContract.StepsEntry.COLUMN_DAY + " DESC"
        );
        boolean hasToday = false;
        Calendar c = Calendar.getInstance();
        while (cursor.moveToNext()) {
            int day = cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_DAY));
            int month = cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_MONTH));
            int year = cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_YEAR));
            int cDay = c.get(Calendar.DAY_OF_MONTH);
            int cMonth = c.get(Calendar.MONTH) + 1;
            int cYear = c.get(Calendar.YEAR);
            if (day == cDay && cMonth == month && cYear == year) hasToday = true;
            days.add(new Day(day, month, year)
                             .setWalking(cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_WALKING)))
                             .setJogging(cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_JOGGING)))
                             .setRunning(cursor.getInt(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_RUNNING)))
                             .setDuration(cursor.getLong(cursor.getColumnIndex(StepsContract.StepsEntry.COLUMN_DURATION))));
        }
        if (!hasToday) days.add(0, Day.fromCalendar(c));
        cursor.close();
        return days;
    }

    public boolean setTargetReached(String columnName, Day fullDay) {
        SQLiteDatabase db = getWritableDatabase();
        int day = fullDay.getDay();
        int month = fullDay.getMonth();
        int year = fullDay.getYear();
        String where =
                StepsContract.StepsEntry.COLUMN_DAY + "=? AND "
                        + StepsContract.StepsEntry.COLUMN_MONTH + "=? AND "
                        + StepsContract.StepsEntry.COLUMN_YEAR + "=?";
        String[] whereArgs = new String[]{
                String.valueOf(day),
                String.valueOf(month),
                String.valueOf(year)
        };
        ContentValues cv = new ContentValues();
        cv.put(columnName, true);
        int updated = db.update(StepsContract.StepsEntry.TABLE_NAME, cv, where, whereArgs);
        return updated > 1;
    }

    public boolean isTargetReached(String columnName, Day fullDay) {
        boolean result = false;
        SQLiteDatabase db = getReadableDatabase();
        int day = fullDay.getDay();
        int month = fullDay.getMonth();
        int year = fullDay.getYear();
        String where =
                StepsContract.StepsEntry.COLUMN_DAY + "=? AND "
                        + StepsContract.StepsEntry.COLUMN_MONTH + "=? AND "
                        + StepsContract.StepsEntry.COLUMN_YEAR + "=?";
        String[] whereArgs = new String[]{
                String.valueOf(day),
                String.valueOf(month),
                String.valueOf(year)
        };
        Cursor cursor = db.query(StepsContract.StepsEntry.TABLE_NAME, new String[]{columnName},
                                 where, whereArgs, null, null, null
        );
        if (cursor.moveToFirst())
            result = cursor.getInt(cursor.getColumnIndex(columnName)) == 1;
        Log.i(TAG, "isTargetReached: " + cursor.getInt(cursor.getColumnIndex(columnName)));
        cursor.close();
        return result;
    }

    public void setListener(StepsListener listener) {
        mListener = listener;
    }
}
