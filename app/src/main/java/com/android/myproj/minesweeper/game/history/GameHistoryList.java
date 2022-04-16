package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

// Singleton
public class GameHistoryList {

    private static GameHistoryList gameHistoryListInstance;

    private final List<GameHistoryVo> easyHistoryList;
    private final List<GameHistoryVo> intermediateHistoryList;
    private final List<GameHistoryVo> expertHistoryList;
    private final List<GameHistoryVo> jumboHistoryList;
    private final List<CustomHistoryVo> customHistoryList;

    private GameHistoryList() {
        this.easyHistoryList = new ArrayList<>();
        this.intermediateHistoryList = new ArrayList<>();
        this.expertHistoryList = new ArrayList<>();
        this.jumboHistoryList = new ArrayList<>();
        this.customHistoryList = new ArrayList<>();
    }

    public GameHistoryVo getGameHistory(int index, Level level) {
        return switch (level) {
            case CUSTOM -> this.customHistoryList.get(index);
            default -> this.getLevelHistoryList(level).get(index);
        };
    }

    public int size(Level level) {
        return switch (level) {
            case CUSTOM -> this.customHistoryList.size();
            default -> this.getLevelHistoryList(level).size();
        };
    }

    // Returns the index of insertion
    public void addGameHistory(GameHistoryVo gameHistory, Level level) {
        // If Custom level, do not sort history
        if (level == Level.CUSTOM) {
            this.customHistoryList.add((CustomHistoryVo) gameHistory);
            return;
        }

        List<GameHistoryVo> gameHistoryList = this.getLevelHistoryList(level);
        // Add in front of the first instance where list item is less than given gameHistory
        for (int i = 0; i < gameHistoryList.size(); i++) {
            if (gameHistory.compareTo(gameHistoryList.get(i)) < 0) {
                gameHistoryList.add(i, gameHistory);
                return;
            }
        }
        // If not added, add to end of list
        gameHistoryList.add(gameHistory);
    }

    public void resetGameHistory(Level level) {
        if (level == Level.CUSTOM) {
            this.customHistoryList.clear();
        } else {
            this.getLevelHistoryList(level).clear();
        }
    }

    private List<GameHistoryVo> getLevelHistoryList(Level level) {
        return switch (level) {
            case EASY -> this.easyHistoryList;
            case INTERMEDIATE -> this.intermediateHistoryList;
            case EXPERT -> this.expertHistoryList;
            case JUMBO -> this.jumboHistoryList;
            default -> throw new RuntimeException();
        };
    }

    public void saveHistoryList(JSONObject savedHistory) throws JSONException {
        for (Level level : Level.values()) {
            JSONArray savedLevelHistory = new JSONArray();
            List<? extends GameHistoryVo> historyList = level == Level.CUSTOM
                    ? this.customHistoryList
                    : this.getLevelHistoryList(level);
            for (int index = 0; index < historyList.size(); index++) {
                savedLevelHistory.put(historyList.get(index).saveGameHistory());
            }
            savedHistory.put(JSONKey.getHistoryKey(level), savedLevelHistory);
        }
    }

    public static void restoreSavedHistory(JSONObject savedHistory) throws ParseException, JSONException {
        // Do not restore data if an instance of GameHistoryList exists
        if (gameHistoryListInstance != null) {
            return;
        }

        GameHistoryList singleton = getInstance();  // always called when gameHistoryListInstance == null
        for (Level level : Level.values()) {
            if (level == Level.CUSTOM) {
                restoreSavedHistoryCustom(savedHistory, singleton);
                continue;
            }

            List<GameHistoryVo> historyList = singleton.getLevelHistoryList(level);  // therefore, always empty
            JSONArray savedLevelHistory = savedHistory.getJSONArray(JSONKey.getHistoryKey(level));
            for (int index = 0; index < savedLevelHistory.length(); index++) {
                historyList.add(GameHistoryVo.restoreGameHistory(savedLevelHistory.getJSONArray(index)));
            }
        }
    }

    private static void restoreSavedHistoryCustom(JSONObject savedHistory, GameHistoryList singleton)
            throws ParseException, JSONException {
        JSONArray savedLevelHistory = savedHistory.getJSONArray(JSONKey.getHistoryKey(Level.CUSTOM));
        for (int index = 0; index < savedLevelHistory.length(); index++) {
            singleton.customHistoryList.add(CustomHistoryVo.restoreGameHistory(savedLevelHistory.getJSONArray(index)));
        }
    }

    public static GameHistoryList getInstance() {
        if (gameHistoryListInstance == null) {
            gameHistoryListInstance = new GameHistoryList();
        }
        return gameHistoryListInstance;
    }

}
