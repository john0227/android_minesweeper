package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.Date;

public class CustomHistoryVo extends GameHistoryVo {

    private final int rows;
    private final int cols;
    private final int mines;

    public CustomHistoryVo(int resultCode) {
        this(new Date(), resultCode, resultCode, resultCode);
        // Should only be called if game was lost
        assert resultCode == GAME_LOST || resultCode == GAME_NOT_RESUMED;
    }

    public CustomHistoryVo(int minute, int second, int millis) {
        this(new Date(), minute, second, millis, Level.CUSTOM.getRow(), Level.CUSTOM.getCol(), Level.CUSTOM.getMines());
    }

    public CustomHistoryVo(Date date, int minute, int second, int millis) {
        this(date, minute, second, millis, Level.CUSTOM.getRow(), Level.CUSTOM.getCol(), Level.CUSTOM.getMines());
    }

    private CustomHistoryVo(Date date, int minute, int second, int millis, int rows, int cols, int mines) {
        super(date, minute, second, millis);
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
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

    @Override
    public JSONArray saveGameHistory() {
        JSONArray gameHistoryJSON = super.saveGameHistory();
        gameHistoryJSON.put(this.rows);
        gameHistoryJSON.put(this.cols);
        gameHistoryJSON.put(this.mines);
        return gameHistoryJSON;
    }

    public static CustomHistoryVo restoreGameHistory(JSONArray gameHistoryJSON) throws ParseException, JSONException {
        // Assume given JSONArray is a valid GameHistoryJSONArray
        return new CustomHistoryVo(
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
