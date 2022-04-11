package com.android.myproj.minesweeper.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class Stopwatch {

    private final TextView stopwatchView;
    private final Handler stopwatchHandler;
    private int timeSeconds;
    private int timeMinutes;
    private int timeMillis;
    private boolean isRunning;
    private boolean isPaused;

    public Stopwatch(TextView stopwatchView) {
        this(stopwatchView, 0, 0, 0);
    }

    public Stopwatch(TextView stopwatchView, int timeMinutes, int timeSeconds, int timeMillis) {
        this.stopwatchView = stopwatchView;
        this.stopwatchHandler = new Handler(Looper.getMainLooper());
        this.timeMinutes = timeMinutes;
        this.timeSeconds = timeSeconds;
        this.timeMillis = timeMillis;
        this.stopwatchView.setText(formatTime());
        this.isRunning = false;
        this.isPaused = false;
    }

    public int getTimeMillis() {
        return this.timeMillis;
    }

    public int getTimeSeconds() {
        return this.timeSeconds;
    }

    public int getTimeMinutes() {
        return this.timeMinutes;
    }

    public int getTotalTimeInSeconds() {
        // Returning int instead of long
        // Because for this value to overflow,
        // Approx, this.timeMinutes * 60 must be greater than 2,000,000,000
        // which means player must have been playing for 2,000,000,000 / 60 (approx 33,333,333) minutes
        // which means player must have been playing for 33,333,333 / 60 (approx 555,555) hours
        // which means player must have been playing for 555,555 / 24 (approx 23,148) days (which I deemed highly unlikely)
        return this.timeMinutes * 60 + this.timeSeconds;
    }

    public void startTimer() {
        if (this.isRunning) {
            return;
        }
        this.isRunning = true;
        this.isPaused = false;
        this.stopwatchHandler.post(run_timer);
    }

    public void resumeTimer() {
        if (this.isRunning || !this.isPaused) {
            return;
        }
        this.isRunning = true;
        this.isPaused = false;
        this.stopwatchHandler.post(run_timer);
    }

    public void pauseTimer() {
        if (!this.isRunning) {
            return;
        }
        this.isRunning = false;
        this.isPaused = true;
        this.stopwatchHandler.removeCallbacks(run_timer);
    }

    public void destroyTimer() {
        this.pauseTimer();
    }

    private String formatTime() {
        if (timeMinutes > 0) {
            return String.format("%d:%02d", timeMinutes, timeSeconds);
        } else {
            return "" + timeSeconds;
        }
    }

    private final Runnable run_timer = new Runnable() {
        @Override
        public void run() {
            // Set the text view text.
            stopwatchView.setText(formatTime());

            timeMillis += 100;

            if (timeMillis >= 1000) {
                timeMillis -= 1000;
                timeSeconds++;
            }
            if (timeSeconds >= 60) {
                timeSeconds -= 60;
                timeMinutes++;
            }
            // Post the code again
            // with a delay of 1 second.
            stopwatchHandler.postDelayed(this, 100);
        }
    };

}
