package com.android.myproj.minesweeper.util;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class Stopwatch {

    private final TextView stopwatchView;
    private final Handler stopwatchHandler;
    private int timeSeconds;
    private int timeMinutes;
    private boolean isRunning;
    private boolean isPaused;

    public Stopwatch(TextView stopwatchView) {
        this(stopwatchView, 0, 0);
    }

    public Stopwatch(TextView stopwatchView, int timeMinutes, int timeSeconds) {
        this.stopwatchView = stopwatchView;
        this.stopwatchHandler = new Handler(Looper.getMainLooper());
        this.timeMinutes = timeMinutes;
        this.timeSeconds = timeSeconds;
        this.stopwatchView.setText(formatTime());
        this.isRunning = false;
        this.isPaused = false;
    }

    public int getTimeSeconds() {
        return this.timeSeconds;
    }

    public int getTimeMinutes() {
        return this.timeMinutes;
    }

    public long getTotalTimeInSeconds() {
        return this.timeMinutes * 60L + this.timeSeconds;
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
        this.destroyTimer();
    }

    public void destroyTimer() {
        if (!this.isRunning) {
            return;
        }
        this.isRunning = false;
        this.isPaused = true;
        this.stopwatchHandler.removeCallbacks(run_timer);
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
            if (timeSeconds >= 60) {
                timeSeconds -= 60;
                timeMinutes++;
            }

            // Set the text view text.
            stopwatchView.setText(formatTime());

            timeSeconds++;

            // Post the code again
            // with a delay of 1 second.
            stopwatchHandler.postDelayed(this, 1000);
        }
    };

}
