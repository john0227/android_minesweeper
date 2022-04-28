package com.android.myproj.minesweeper.game.logic;

import com.android.myproj.minesweeper.config.JSONKey;

import org.json.JSONException;
import org.json.JSONObject;

public enum Level {

    EASY(10, 10, 10, 75, 1),
    INTERMEDIATE(13, 15, 40, 30, 2),
    EXPERT(16, 30, 99, 10, 3),
    JUMBO(25, 35, 185, 10, 4),
    CUSTOM(10, 10, 10, 50, 5);

    private int col;
    private int row;
    private int mines;
    private long animationDelay;
    private final int code;

    Level(final int col, final int row, final int mines, final int animation_delay, final int code) {
        this.col = col;
        this.row = row;
        this.mines = mines;
        this.animationDelay = animation_delay;
        this.code = code;
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public int getMines() {
        return this.mines;
    }

    public long getAnimationDelay() {
        return this.animationDelay;
    }

    public int getCode() {
        return this.code;
    }

    public void setValues(int col, int row, int mines, long animationDelay) {
        if (this != CUSTOM) {
            return;
        }
        this.col = col;
        this.row = row;
        this.mines = mines;
        this.animationDelay = animationDelay;
    }

    public void saveLevel(JSONObject savedData) throws JSONException {
        savedData.put(JSONKey.KEY_LEVEL, this.code);

        if (this != CUSTOM) {
            // Remove JSON keys associated with CUSTOM level, if present
            savedData.remove(JSONKey.KEY_LEVEL_COL);
            savedData.remove(JSONKey.KEY_LEVEL_ROW);
            savedData.remove(JSONKey.KEY_LEVEL_MINES);
            savedData.remove(JSONKey.KEY_LEVEL_DELAY);
            return;
        }

        // If this level is CUSTOM
        savedData.put(JSONKey.KEY_LEVEL_COL, this.col);
        savedData.put(JSONKey.KEY_LEVEL_ROW, this.row);
        savedData.put(JSONKey.KEY_LEVEL_MINES, this.mines);
        savedData.put(JSONKey.KEY_LEVEL_DELAY, this.animationDelay);
    }

    public static Level restoreLevel(JSONObject savedData) throws JSONException {
        Level level = Level.getLevelFromCode(savedData.getInt(JSONKey.KEY_LEVEL));
        if (level != CUSTOM) {
            return level;
        }
        level.setValues(
                savedData.getInt(JSONKey.KEY_LEVEL_COL),
                savedData.getInt(JSONKey.KEY_LEVEL_ROW),
                savedData.getInt(JSONKey.KEY_LEVEL_MINES),
                savedData.getInt(JSONKey.KEY_LEVEL_DELAY)
        );
        return level;
    }

    public static Level getLevelFromCode(int code) {
        return switch (code) {
            case 1 -> Level.EASY;
            case 2 -> Level.INTERMEDIATE;
            case 3 -> Level.EXPERT;
            case 4 -> Level.JUMBO;
            case 5 -> Level.CUSTOM;
            default -> throw new RuntimeException("Invalid code to get Level: " + code);
        };
    }

}
