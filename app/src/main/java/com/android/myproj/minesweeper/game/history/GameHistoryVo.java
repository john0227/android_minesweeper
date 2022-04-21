package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameHistoryVo {

    public static final int GAME_NOT_RESUMED = Integer.MAX_VALUE - 1;
    public static final int GAME_LOST = Integer.MAX_VALUE;
    protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS", Locale.US);

    protected final Date date;
    protected final int minute;
    protected final int second;
    protected final int millis;
    private final int rows;
    private final int cols;
    private final int mines;

    public GameHistoryVo(int resultCode, Level level) {
        this(new Date(), resultCode, resultCode, resultCode, level);
        // Should only be called if game was lost or not resumed
        assert resultCode == GAME_NOT_RESUMED || resultCode == GAME_LOST;
    }

    public GameHistoryVo(int minute, int second, int millis, Level level) {
        this(new Date(), minute, second, millis, level);
    }

    public GameHistoryVo(Date date, int minute, int second, int millis, Level level) {
        this(date, minute, second, millis, level.getRow(), level.getCol(), level.getMines());
    }

    private GameHistoryVo(Date date, int minute, int second, int millis, int rows, int cols, int mines) {
        this.date = date;
        this.minute = minute;
        this.second = second;
        this.millis = millis;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
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

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public int getMines() {
        return this.mines;
    }

    public boolean wasWon() {
        return this.minute != GAME_LOST && this.minute != GAME_NOT_RESUMED;
    }

    public JSONArray saveGameHistory() {
        JSONArray gameHistoryJSON = new JSONArray();
        gameHistoryJSON.put(simpleDateFormat.format(this.date));
        gameHistoryJSON.put(this.minute);
        gameHistoryJSON.put(this.second);
        gameHistoryJSON.put(this.millis);
        gameHistoryJSON.put(this.rows);
        gameHistoryJSON.put(this.cols);
        gameHistoryJSON.put(this.mines);
        return gameHistoryJSON;
    }

    public static GameHistoryVo restoreGameHistory(JSONArray gameHistoryJSON) throws ParseException, JSONException {
        // Assume given JSONArray is a valid GameHistoryJSONArray
        return new GameHistoryVo(
                simpleDateFormat.parse(gameHistoryJSON.getString(0)),
                gameHistoryJSON.getInt(1),
                gameHistoryJSON.getInt(2),
                gameHistoryJSON.getInt(3),
                gameHistoryJSON.getInt(4),
                gameHistoryJSON.getInt(5),
                gameHistoryJSON.getInt(6)
        );
    }

}
