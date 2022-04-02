package com.android.myproj.minesweeper.util;

import android.app.Activity;

import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class StatUtil {

    public static void updateStat(Activity activity, Level level, long time, boolean noHint) throws JSONException {
        String keySavedStat = switch (level) {
            case EASY -> JSONKey.KEY_EXISTS_SAVED_EASY_STAT;
            case INTERMEDIATE -> JSONKey.KEY_EXISTS_SAVED_INTERMEDIATE_STAT;
            case EXPERT -> JSONKey.KEY_EXISTS_SAVED_EXPERT_STAT;
        };

        try {
            JSONUtil.createDefaultStatIfNone(activity, keySavedStat);
        } catch (JSONException | IOException e) {
            LogService.error(activity, "Was unable to create default statistics", e);
        }

        JSONObject savedStat = JSONUtil.readJSONFile(activity);
        String[] allLevelKeys = JSONUtil.getAllKeysForLevel(keySavedStat);

        updateGameWon(savedStat, allLevelKeys);
        updateWinRate(savedStat, allLevelKeys);
        updateBestTime(savedStat, allLevelKeys, time);
        updateAvgTime(savedStat, allLevelKeys, time);
        updateCurrStreak(savedStat, allLevelKeys);
        updateBestStreak(savedStat, allLevelKeys);
        updateWinsNoHint(savedStat, allLevelKeys, noHint);
    }

    private static void updateGameWon(JSONObject savedStat, String[] keys) throws JSONException {
        savedStat.put(keys[1], savedStat.getLong(keys[1]) + 1);
    }

    private static void updateWinRate(JSONObject savedStat, String[] keys) throws JSONException {
        long totalGames = savedStat.getLong(keys[0]);
        long gamesWon = savedStat.getLong(keys[1]);
        int winRate = (int) ((totalGames / gamesWon) * 100);
        savedStat.put(keys[2], winRate);
    }

    private static void updateBestTime(JSONObject savedStat, String[] keys, long time) throws JSONException {
        long bestTime = savedStat.getLong(keys[3]);
        if (time < bestTime) {
            savedStat.put(keys[3], time);
        }
    }

    private static void updateAvgTime(JSONObject savedStat, String[] keys, long time) throws JSONException {
        long currAvg = savedStat.getLong(keys[4]);
        long totalGames = savedStat.getLong(keys[0]);
        long newAvg = currAvg + (time - currAvg) / (totalGames);
        savedStat.put(keys[4], newAvg);
    }

    private static void updateCurrStreak(JSONObject savedStat, String[] keys) throws JSONException {
        savedStat.put(keys[5], savedStat.getLong(keys[5]) + 1);
    }

    private static void updateBestStreak(JSONObject savedStat, String[] keys) throws JSONException {
        long currStreak = savedStat.getLong(keys[5]);
        long bestStreak = savedStat.getLong(keys[6]);
        if (currStreak > bestStreak) {
            savedStat.put(keys[6], currStreak);
        }
    }

    private static void updateWinsNoHint(JSONObject savedStat, String[] keys, boolean noHint) throws JSONException {
        if (!noHint) {
            return;
        }
        savedStat.put(keys[7], savedStat.getLong(keys[7]) + 1);
    }

}
