package com.android.myproj.minesweeper.game.history;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameHistoryVo implements Comparable<GameHistoryVo> {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private final Date date;
    private final int minute;
    private final int second;
    private final int millis;

    public GameHistoryVo(int minute, int second, int millis) {
        this(new Date(), minute, second, millis);
    }

    public GameHistoryVo(Date date, int minute, int second, int millis) {
        this.date = date;
        this.minute = minute;
        this.second = second;
        this.millis = millis;
    }

    public Date getDate() {
        return this.date;
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

    public JSONArray saveGameHistory() {
        JSONArray gameHistoryJSON = new JSONArray();
        gameHistoryJSON.put(simpleDateFormat.format(this.date));
        gameHistoryJSON.put(this.minute);
        gameHistoryJSON.put(this.second);
        gameHistoryJSON.put(this.millis);
        return gameHistoryJSON;
    }

    public static GameHistoryVo restoreGameHistory(JSONArray gameHistoryJSON) throws ParseException, JSONException {
        // Assume given JSONArray is a valid GameHistoryJSONArray
        return new GameHistoryVo(
                simpleDateFormat.parse(gameHistoryJSON.getString(0)),
                gameHistoryJSON.getInt(1),
                gameHistoryJSON.getInt(2),
                gameHistoryJSON.getInt(3)
        );
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
