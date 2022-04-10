package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.game.logic.Level;

import java.util.Date;

public class GameHistoryVo implements Comparable<GameHistoryVo> {

    private final Date date;
    private final Level level;
    private final int minute;
    private final int second;
    private final int millis;

    public GameHistoryVo(Level level, int minute, int second, int millis) {
        this.date = new Date();
        this.level = level;
        this.minute = minute;
        this.second = second;
        this.millis = millis;
    }

    public Date getDate() {
        return this.date;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getMillis() {
        return millis;
    }

    @Override
    public int compareTo(GameHistoryVo gameHistory) {
        if (gameHistory == null) {
            return 1;
        }

        if (this.minute > gameHistory.minute) {
            return 1;
        } else if (this.minute < gameHistory.minute) {
            return -1;
        }

        // this.minute == gameHistory.minute
        if (this.second > gameHistory.second) {
            return 1;
        } else if (this.second < gameHistory.second) {
            return -1;
        }

        // this.minute == gameHistory.minute && this.second == gameHistory.second
        if (this.millis > gameHistory.millis) {
            return 1;
        } else if (this.millis < gameHistory.millis) {
            return -1;
        }

        // this.minute == gameHistory.minute && this.second == gameHistory.second && this.millis == gameHistory.millis
        return this.date.compareTo(gameHistory.date);
    }
}
