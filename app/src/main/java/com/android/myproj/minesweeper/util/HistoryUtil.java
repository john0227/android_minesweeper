package com.android.myproj.minesweeper.util;

import android.app.Activity;

import com.android.myproj.minesweeper.activity.MinesweeperGameActivity;
import com.android.myproj.minesweeper.game.history.CustomHistoryVo;
import com.android.myproj.minesweeper.game.history.GameHistoryList;
import com.android.myproj.minesweeper.game.history.GameHistoryVo;
import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HistoryUtil {

    public static boolean isResettable(Level level) {
        // Retrieve GameHistoryList instance
        GameHistoryList singleton = GameHistoryList.getInstance();
        return singleton.size(level) > 0;
    }

    public static void resetHistory(Level level) {
        // Retrieve GameHistoryList instance
        GameHistoryList singleton = GameHistoryList.getInstance();
        singleton.resetGameHistory(level);
    }

    public static void saveGameHistory(Activity activity, Level level, Stopwatch stopwatch) throws JSONException, IOException {
        // Retrieve GameHistoryList instance
        GameHistoryList singleton = GameHistoryList.getInstance();
        // Update GameHistoryList
        GameHistoryVo gameHistoryVo;
        if (level == Level.CUSTOM) {
            gameHistoryVo = new CustomHistoryVo(
                    stopwatch.getTimeMinutes(),
                    stopwatch.getTimeSeconds(),
                    stopwatch.getTimeMillis()
            );
        } else {
            gameHistoryVo = new GameHistoryVo(
                    stopwatch.getTimeMinutes(),
                    stopwatch.getTimeSeconds(),
                    stopwatch.getTimeMillis()
            );
        }
        singleton.addGameHistory(gameHistoryVo, level);
        // Save GameHistoryList to JSON
        JSONObject savedData = JSONUtil.readJSONFile(activity);
        singleton.saveHistoryList(savedData);
        JSONUtil.writeToJSONFile(activity, savedData);
    }

    public static void saveGameHistory(JSONObject savedData, Level level, int gameCode)
            throws JSONException, IOException {
        assert gameCode == GameHistoryVo.GAME_LOST || gameCode == GameHistoryVo.GAME_NOT_RESUMED;

        // Retrieve GameHistoryList instance
        GameHistoryList singleton = GameHistoryList.getInstance();
        // Update GameHistoryList
        GameHistoryVo gameHistoryVo;
        if (level == Level.CUSTOM) {
            gameHistoryVo = new CustomHistoryVo(gameCode);
        } else {
            gameHistoryVo = new GameHistoryVo(gameCode);
        }
        singleton.addGameHistory(gameHistoryVo, level);
        // Save GameHistoryList to JSON
        singleton.saveHistoryList(savedData);
    }

}
