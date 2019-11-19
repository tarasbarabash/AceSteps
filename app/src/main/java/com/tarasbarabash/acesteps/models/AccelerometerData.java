package com.tarasbarabash.acesteps.models;

import android.os.SystemClock;
import androidx.annotation.NonNull;

/**
 * Created by Taras
 * 12-Sep-19, 20:18.
 */
public class AccelerometerData {
    private float x,y,z;
    private double value;
    private long time;
    private boolean isPeak;

    public AccelerometerData(float x, float y, float z, long time) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = time / 1000000 + (System.currentTimeMillis() - SystemClock.elapsedRealtime());
        this.value = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        this.isPeak = true;
    }

    public void setPeak(boolean peak) {
        isPeak = peak;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public double getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }

    public boolean isPeak() {
        return isPeak;
    }

    @NonNull
    @Override
    public String toString() {
        return "time: " + time + ", x: " + x + ", y: " + y + ", z: " + z + ", value:" + value;
    }
}
