package com.android.myproj.minesweeper.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class Stopwatch {

    private final TextView stopwatchView;
    private final Handler stopwatchHandler;
    private int timeSeconds;
    private int timeMinutes;
    private long startTime;
    private long endTime;
    private boolean isRunning;
    private boolean isPaused;

    public Stopwatch(TextView stopwatchView) {
        this(stopwatchView, 0, 0, System.currentTimeMillis());
    }

    public Stopwatch(TextView stopwatchView, int timeMinutes, int timeSeconds, long startTime) {
        this.stopwatchView = stopwatchView;
        this.stopwatchHandler = new Handler(Looper.getMainLooper());
        this.timeMinutes = timeMinutes;
        this.timeSeconds = timeSeconds;
        this.startTime = startTime;
        this.stopwatchView.setText(formatTime());
        this.isRunning = false;
        this.isPaused = false;
    }

    public long getStartTime() {
        return this.startTime;
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
        return this.endTime - this.startTime;
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
        this.endTime = System.currentTimeMillis();
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

            timeSeconds++;

            if (timeSeconds >= 60) {
                timeSeconds -= 60;
                timeMinutes++;
            }
            // Post the code again
            // with a delay of 1 second.
            stopwatchHandler.postDelayed(this, 1000);
        }
    };

}
