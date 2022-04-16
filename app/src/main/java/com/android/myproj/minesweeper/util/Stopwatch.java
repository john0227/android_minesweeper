package com.android.myproj.minesweeper.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.myproj.minesweeper.config.JSONKey;

import org.json.JSONException;
import org.json.JSONObject;

public class Stopwatch {

    private final TextView stopwatchView;
    private final Handler stopwatchHandler;
    private long startTime;
    private long endTime;
    private long storedTime;
    private boolean isRunning;
    private boolean isPaused;

    public Stopwatch(TextView stopwatchView) {
        this.stopwatchView = stopwatchView;
        this.stopwatchHandler = new Handler(Looper.getMainLooper());
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
        this.storedTime = 0;
        this.stopwatchView.setText(formatTime());
        this.isRunning = false;
        this.isPaused = false;
    }

    public void saveStopwatch(JSONObject savedData) throws JSONException {
        savedData.put(JSONKey.KEY_TIME_STORED_MILLIS, this.getTotalTimeInMillis());
    }

    public void restoreStopwatch(JSONObject savedData) throws JSONException {
        this.storedTime = savedData.getLong(JSONKey.KEY_TIME_STORED_MILLIS);
        this.stopwatchView.setText(formatTime());
    }

    public int getTimeMillis() {
        return (int) (this.getTotalTimeInMillis() % 1000);
    }

    public int getTimeSeconds() {
        return this.getTotalTimeInSeconds() % 60;
    }

    public int getTimeMinutes() {
        return this.getTotalTimeInSeconds() / 60;
    }

    public int getTotalTimeInSeconds() {
        // Returning int instead of long
        // Because for this value to overflow,
        // Approx, this.timeMinutes * 60 must be greater than 2,000,000,000
        // which means player must have been playing for 2,000,000,000 / 60 (approx 33,333,333) minutes
        // which means player must have been playing for 33,333,333 / 60 (approx 555,555) hours
        // which means player must have been playing for 555,555 / 24 (approx 23,148) days (which I deemed highly unlikely)
        return (int) (this.getTotalTimeInMillis() - this.getTimeMillis()) / 1000;
    }

    public long getTotalTimeInMillis() {
        if (this.endTime == 0) {
            return 0;
        }
        return this.endTime - this.startTime + this.storedTime;
    }

    public void startTimer() {
        if (this.isRunning) {
            return;
        }
        this.isRunning = true;
        this.isPaused = false;
        this.startTime = System.currentTimeMillis();
        this.stopwatchHandler.post(run_timer);
    }

    public void resumeTimer() {
        if (this.isRunning || !this.isPaused) {
            return;
        }
        this.isRunning = true;
        this.isPaused = false;
        this.startTime = System.currentTimeMillis();
        this.stopwatchHandler.post(run_timer);
    }

    public void pauseTimer() {
        if (!this.isRunning) {
            return;
        }
        this.isRunning = false;
        this.isPaused = true;
        this.endTime = System.currentTimeMillis();
        stopwatchView.setText(formatTime());
        this.stopwatchHandler.removeCallbacks(run_timer);
    }

    public void destroyTimer() {
        this.pauseTimer();
    }

    @NonNull
    @Override
    public String toString() {
        return TimeFormatUtil.formatTime(this.getTimeMinutes(), this.getTimeSeconds(), this.getTimeMillis());
    }

    private String formatTime() {
        if (this.getTimeMinutes() > 0) {
            return TimeFormatUtil.formatTime(this.getTimeMinutes(), this.getTimeSeconds());
        } else {
            return "" + this.getTimeSeconds();
        }
    }

    private final Runnable run_timer = new Runnable() {
        @Override
        public void run() {
            // Set the text view text.
            endTime = System.currentTimeMillis();
            stopwatchView.setText(formatTime());

            // Post the code again with a delay of 300 ms
            stopwatchHandler.postDelayed(this, 300);
        }
    };

}
