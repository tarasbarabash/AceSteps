package com.tarasbarabash.acesteps.Tasks;

import android.util.Log;

import com.tarasbarabash.acesteps.Constants.StepsTypes;
import com.tarasbarabash.acesteps.models.AccelerometerData;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Taras
 * 12-Sep-19, 21:29.
 */
public class ProcessAccelerometerDataTask implements Runnable {
    private ArrayList<AccelerometerData> mData;
    private ArrayList<AccelerometerData> mFractionData;
    private ArrayList<AccelerometerData> mPeaks;
    private StepDetectorInterface mListener;
    private static final String TAG = ProcessAccelerometerDataTask.class.getSimpleName();

    public ProcessAccelerometerDataTask(ArrayList<AccelerometerData> data, StepDetectorInterface listener) {
        mData = data;
        mListener = listener;
    }

    @Override
    public void run() {
        Log.e(TAG, "run: " + getClass().getCanonicalName());
        mFractionData = new ArrayList<>();
        mPeaks = new ArrayList<>();
        Log.e(TAG, "Running");
        mFractionData.addAll(mData);
        mData.clear();
        findPeaks();
        removeFakePeaks();
        mListener.onStepDetected(mPeaks);
        Log.e(TAG, "Finished!");
    }

    private void findPeaks() {
        ArrayList<AccelerometerData> peaks = new ArrayList<>();
        for (AccelerometerData data : mFractionData) {
//            Log.e(TAG, "findPeaks: " + data.getValue());
            if (data.getValue() > StepsTypes.WALKING)
                peaks.add(data);
            else if (peaks.size() > 0) {
                Collections.sort(peaks, (o1, o2) ->
                        o1.getValue() >= o2.getValue() ? 1 : -1
                );
                mPeaks.add(peaks.get(peaks.size() - 1));
                peaks.clear();
            }
        }
    }

    private void removeFakePeaks() {
        for (int i = 0; i < mPeaks.size() - 1; i++) {
            AccelerometerData cur = mPeaks.get(i);
            AccelerometerData next = mPeaks.get(i + 1);
            if (cur.isPeak()) {
                if (next.getTime() - cur.getTime() < 300) {
                    if (next.getValue() > cur.getValue()) cur.setPeak(false);
                    else next.setPeak(false);
                }
            }
        }
        ArrayList<AccelerometerData> realPeaks = new ArrayList<>();
        for (AccelerometerData data : mPeaks) if (data.isPeak()) realPeaks.add(data);
        mPeaks.clear();
        mPeaks.addAll(realPeaks);
    }

    public interface StepDetectorInterface {
        void onStepDetected(ArrayList<AccelerometerData> accelerometerData);
    }
}
