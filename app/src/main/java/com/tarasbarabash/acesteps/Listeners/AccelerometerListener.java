package com.tarasbarabash.acesteps.Listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.tarasbarabash.acesteps.models.AccelerometerData;

/**
 * Created by Taras
 * 12-Sep-19, 20:17.
 */
public class AccelerometerListener implements SensorEventListener {
    private AccelerometerListenerInterface mListenerInterface;

    public AccelerometerListener(AccelerometerListenerInterface listenerInterface) {
        mListenerInterface = listenerInterface;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        long time = event.timestamp;
        mListenerInterface.onChange(new AccelerometerData(x, y, z, time));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface AccelerometerListenerInterface {
        void onChange(AccelerometerData data);
    }
}
