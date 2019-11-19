package com.tarasbarabash.acesteps.viewModels;

import androidx.core.content.ContextCompat;

import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.tarasbarabash.acesteps.Constants.SharedPreferencesNames;
import com.tarasbarabash.acesteps.Constants.TasksConstants;
import com.tarasbarabash.acesteps.Database.WorkoutDBHelper;
import com.tarasbarabash.acesteps.Fragments.BaseFragmentWithMenu;
import com.tarasbarabash.acesteps.R;
import com.tarasbarabash.acesteps.Utilities.SharedPrefsUtils;
import com.tarasbarabash.acesteps.databinding.FragmentWorkoutBinding;
import com.tarasbarabash.acesteps.models.WorkoutSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Taras
 * 20-Sep-19, 18:35.
 */
public class WorkoutVM extends FragmentWithMenuVM<FragmentWorkoutBinding> {
    private static final String TAG = WorkoutVM.class.getSimpleName();
    private WorkoutSession mWorkoutSession;
    private WorkoutDBHelper mDb;

    public WorkoutVM(FragmentWorkoutBinding binding, BaseFragmentWithMenu fragment,
                     String workoutId) {
        super(binding, fragment);
        mDb = WorkoutDBHelper.getInstance(getContext());
        mWorkoutSession = mDb.getWorkoutById(workoutId);
        if (mWorkoutSession == null) {
            Toast.makeText(getContext(), getContext().getString(R.string.workout_not_found), Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }
        setChartDate();
        setToolbarText(getContext().getString(mWorkoutSession.getWorkoutTitle()));
    }

    private void setChartDate() {
        LineChart lineChart = getBinding().chart;
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGridColor(R.color.color_gray);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return mWorkoutSession.getDurationOnlyMinsAndSecs((long) value);
            }
        });
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        leftAxis.setGridColor(R.color.color_gray);
        rightAxis.setDrawLabels(false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.enableGridDashedLine(5, 1, 0);
        lineChart.setClickable(false);
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Float> speeds = mWorkoutSession.getAvgSpeed();
        for (int i = 0; i < speeds.size(); i++) {
            if (i == 0) entries.add(new Entry(0, 0));
            else entries.add(new Entry((i * TasksConstants.PROCESSOR_DELAY), speeds.get(i - 1)));
        }
        LineDataSet set = new LineDataSet(
                entries,
                getContext().getString(R.string.average_speed_title)
        );
        set.setLineWidth(3f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setDrawHighlightIndicators(false);
        set.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData data = new LineData(set);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    public String getDateDurationString() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setTime(mWorkoutSession.getStartTime());
        calendar.setTime(date);
        String dateString =
                new SimpleDateFormat("EEE, dd MMMM", Locale.getDefault()).format(calendar.getTime());
        String startTime = mWorkoutSession.getTimeString(mWorkoutSession.getStartTime());
        String endTime = mWorkoutSession.getTimeString(mWorkoutSession.getEndTime());
        return dateString + ", " + startTime + " - " + endTime;
    }

    public int getHours() {
        return mWorkoutSession.getDurationHours(mWorkoutSession.getLengthInMillis());
    }

    public int getMinutes() {
        return mWorkoutSession.getDurationMinutes(mWorkoutSession.getLengthInMillis());
    }

    public int getSeconds() {
        return mWorkoutSession.getDurationSeconds(mWorkoutSession.getLengthInMillis());
    }

    public String getSpeed() {
        ArrayList<Float> speeds = mWorkoutSession.getAvgSpeed();
        Float speed = 0f;
        for(Float f : speeds) speed += f;
        speed /= speeds.size();
        return String.format(
                Locale.getDefault(),
                "%.02f " + getContext().getString(R.string.speed_caption),
                speed
        );
    }

    public String getDistance() {
        int height = SharedPrefsUtils.getIntStoredAsString(getContext(), SharedPreferencesNames.HEIGHT, 176, null);
        return String.format(
                Locale.getDefault(),
                "%.02f " + getContext().getString(R.string.km_caption),
                mWorkoutSession.getDistance(height)
        );
    }

    public String getCaloriesBurnt() {
        return String.valueOf(mWorkoutSession.getCaloriesBurnt());
    }

    public String getTotalSteps() {
        return String.valueOf(mWorkoutSession.getTotalSteps());
    }

    public String getPace() {
        int height = SharedPrefsUtils.getIntStoredAsString(getContext(), SharedPreferencesNames.HEIGHT, 176, null);
        return mWorkoutSession.getAveragePace(mWorkoutSession.getLengthInMillis(),
                                              mWorkoutSession.getDistance(height));
    }

    @Override
    public boolean hasFragmentOptionsMenu() {
        return true;
    }

    @Override
    public int getMenuLayout() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (mWorkoutSession.getEndTime() > calendar.getTime().getTime())
            return R.menu.menu_session;
        return -1;
    }

    @Override
    public boolean onMenuOptionSelected(int itemId) {
        switch (itemId) {
            case R.id.remove_session:
                boolean result = mDb.removeWorkout(mWorkoutSession.getId().toString());
                if (result) getActivity().finish();
                else Toast.makeText(getContext(), "Workout removal failed!", Toast.LENGTH_LONG)
                        .show();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return true;
        }
    }
}
