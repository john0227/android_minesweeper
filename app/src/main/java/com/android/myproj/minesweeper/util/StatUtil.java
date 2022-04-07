package com.android.myproj.minesweeper.util;

import android.app.Activity;

import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatUtil {

    // Result codes for resetting statistics
    public static final int RES_ERROR_WHILE_RESETTING = 0b0001;  // If exception was raised while resetting
    public static final int RES_RESET                 = 0b0010;  // If statistics has been reset
    public static final int RES_NOTHING_TO_RESET      = 0b0100;  // If there is nothing to reset (GAME_STARTED == 0)
    public static final int RES_NO_RESET              = 0b1000;  // If User opted to not reset after AlertDialog

    public static boolean equalsResCode(int result, int resCode) {
        return (result | resCode) == resCode;
    }

    public static boolean isResettable(Activity activity, Level level) {
        // Check if GAMES_STARTED stat is nonzero
        return getStatistics(activity, level).get(0) != 0;
    }

    public static boolean isResettable(Activity activity) {
        return isResettable(activity, Level.EASY)
                || isResettable(activity, Level.INTERMEDIATE)
                || isResettable(activity, Level.EXPERT);
    }

    public static int resetStat(Activity activity, Level level) {
        try {
            if (isResettable(activity, level)) {
                String keySavedStat = getSavedStatKey(level);
                JSONUtil.createDefaultStat(activity, keySavedStat);
                return RES_RESET;
            }
            return RES_NOTHING_TO_RESET;
        } catch (JSONException | IOException e) {
            return RES_ERROR_WHILE_RESETTING;
        }
    }

    public static int resetAllStats(Activity activity) {
        int hasReset = 0;
        for (int i = 1; i <= 3; i++) {
            hasReset = resetStat(activity, Level.getLevelFromCode(i)) | hasReset;
        }
        return (hasReset & RES_RESET) == RES_RESET ? RES_RESET : hasReset;
    }

    public static void updateAllStat(Activity activity, Level level, int time, boolean noHint)
            throws JSONException, IOException {
        String keySavedStat = getSavedStatKey(level);

        try {
            JSONUtil.createDefaultStatIfNone(activity, keySavedStat);
        } catch (JSONException | IOException e) {
            LogService.error(activity, "Was unable to create default statistics", e);
        }

        JSONObject savedStat = JSONUtil.readJSONFile(activity);
        String[] allLevelKeys = getAllLevelKeys(level);

        // Only update statistics if GAMES_STARTED_STAT != 0
        // Because the user may reset statistics AFTER starting a game
        // -> this would reset GAMES_START_STAT and all other stats to 0
        // -> but if the player wins a game, the stats below will be updated while GAMES_START_STAT remains zero
        if (getStatistics(activity, level).get(0) != 0) {
            updateGameWon(savedStat, allLevelKeys);
            updateWinRate(savedStat, allLevelKeys);
            updateBestTime(savedStat, allLevelKeys, time);
            updateAvgTime(savedStat, allLevelKeys, time);
            updateCurrStreak(savedStat, allLevelKeys);
            updateBestStreak(savedStat, allLevelKeys);
            updateWinsNoHint(savedStat, allLevelKeys, noHint);

            JSONUtil.writeToJSONFile(activity, savedStat);
        }
    }

    public static List<Integer> getStatistics(Activity activity, Level level) {
        List<Integer> stats = new ArrayList<>();
        List<Object> values = JSONUtil.readKeysFromFile(activity, getAllLevelKeys(level));
        for (Object value : values) {
            stats.add((Integer) value);
        }
        return stats;
    }

    public static List<Integer> getOverallStatistics(Activity activity) {
        List<Object> easyOverallStat = JSONUtil.readKeysFromFile(
                activity,
                JSONKey.KEY_STAT_EASY_GAMES_STARTED,
                JSONKey.KEY_STAT_EASY_GAMES_WON,
                JSONKey.KEY_STAT_EASY_WIN_RATE
        );
        List<Object> intermediateOverallStat = JSONUtil.readKeysFromFile(
                activity,
                JSONKey.KEY_STAT_INTERMEDIATE_GAMES_STARTED,
                JSONKey.KEY_STAT_INTERMEDIATE_GAMES_WON,
                JSONKey.KEY_STAT_INTERMEDIATE_WIN_RATE
        );
        List<Object> expertOverallStat = JSONUtil.readKeysFromFile(
                activity,
                JSONKey.KEY_STAT_EXPERT_GAMES_STARTED,
                JSONKey.KEY_STAT_EXPERT_GAMES_WON,
                JSONKey.KEY_STAT_EXPERT_WIN_RATE
        );

        List<Integer> overallStat = new ArrayList<>();
        overallStat.add((Integer) easyOverallStat.get(0) + (Integer) intermediateOverallStat.get(0) + (Integer) expertOverallStat.get(0));
        overallStat.add((Integer) easyOverallStat.get(1) + (Integer) intermediateOverallStat.get(1) + (Integer) expertOverallStat.get(1));
        int totalGames = overallStat.get(0);
        int gamesWon = overallStat.get(1);
        int winRate = (int) (((double) gamesWon / totalGames) * 10000);
        overallStat.add(winRate);
        return overallStat;
    }

    public static String getSavedStatKey(Level level) {
        return switch (level) {
            case EASY -> JSONKey.KEY_EXISTS_SAVED_EASY_STAT;
            case INTERMEDIATE -> JSONKey.KEY_EXISTS_SAVED_INTERMEDIATE_STAT;
            case EXPERT -> JSONKey.KEY_EXISTS_SAVED_EXPERT_STAT;
        };
    }

    public static String[] getAllLevelKeys(Level level) {
        return switch (level) {
            case EASY -> JSONKey.KEYS_EASY_STAT;
            case INTERMEDIATE -> JSONKey.KEYS_INTERMEDIATE_STAT;
            case EXPERT -> JSONKey.KEYS_EXPERT_STAT;
        };
    }

    public static void updateGameStarted(JSONObject savedStat, Level level) throws JSONException {
        String[] keys = getAllLevelKeys(level);
        savedStat.put(keys[0], savedStat.getInt(keys[0]) + 1);
    }

    private static void updateGameWon(JSONObject savedStat, String[] keys) throws JSONException {
        savedStat.put(keys[1], savedStat.getInt(keys[1]) + 1);
    }

    public static void updateWinRate(JSONObject savedStat, Level level) throws JSONException {
        updateWinRate(savedStat, getAllLevelKeys(level));
    }

    private static void updateWinRate(JSONObject savedStat, String[] keys) throws JSONException {
        int totalGames = savedStat.getInt(keys[0]);
        int gamesWon = savedStat.getInt(keys[1]);
        int winRate = (int) (((double) gamesWon / totalGames) * 10000);  // save rate with precision up to two decimal places
        savedStat.put(keys[2], winRate);
    }

    private static void updateBestTime(JSONObject savedStat, String[] keys, int time) throws JSONException {
        int bestTime = savedStat.getInt(keys[3]);
        bestTime = bestTime == 0 ? Integer.MAX_VALUE : bestTime;
        if (time < bestTime) {
            savedStat.put(keys[3], time);
        }
    }

    private static void updateAvgTime(JSONObject savedStat, String[] keys, int time) throws JSONException {
        int currAvg = savedStat.getInt(keys[4]);
        currAvg = currAvg == 0 ? time : currAvg;
        int totalGames = savedStat.getInt(keys[0]);
        int newAvg = currAvg + (int) ((double) (time - currAvg) / totalGames);
        savedStat.put(keys[4], newAvg);
    }

    private static void updateCurrStreak(JSONObject savedStat, String[] keys) throws JSONException {
        savedStat.put(keys[6], savedStat.getInt(keys[6]) + 1);
    }

    private static void updateBestStreak(JSONObject savedStat, String[] keys) throws JSONException {
        int currStreak = savedStat.getInt(keys[6]);
        int bestStreak = savedStat.getInt(keys[5]);
        if (currStreak > bestStreak) {
            savedStat.put(keys[5], currStreak);
        }
    }

    private static void updateWinsNoHint(JSONObject savedStat, String[] keys, boolean noHint) throws JSONException {
        if (!noHint) {
            return;
        }
        savedStat.put(keys[7], savedStat.getInt(keys[7]) + 1);
    }

    public static void resetCurrStreak(JSONObject savedStat, Level level) throws JSONException {
        String key = switch (level) {
            case EASY -> JSONKey.KEY_STAT_EASY_CURR_STREAK;
            case INTERMEDIATE -> JSONKey.KEY_STAT_INTERMEDIATE_CURR_STREAK;
            case EXPERT -> JSONKey.KEY_STAT_EXPERT_CURR_STREAK;
        };
        savedStat.put(key, 0);
    }

}
