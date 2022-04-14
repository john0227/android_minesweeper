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

    private GameHistoryList() {
        this.easyHistoryList = new ArrayList<>();
        this.intermediateHistoryList = new ArrayList<>();
        this.expertHistoryList = new ArrayList<>();
        this.jumboHistoryList = new ArrayList<>();
    }

    public GameHistoryVo getGameHistory(int index, Level level) {
        return this.getLevelHistoryList(level).get(index);
    }

    public int size(Level level) {
        return this.getLevelHistoryList(level).size();
    }

    // Returns the index of insertion
    public int addGameHistory(GameHistoryVo gameHistory, Level level) {
        List<GameHistoryVo> gameHistoryList = this.getLevelHistoryList(level);

        // Add in front of the first instance where list item is less than given gameHistory
        for (int i = 0; i < gameHistoryList.size(); i++) {
            if (gameHistory.compareTo(gameHistoryList.get(i)) < 0) {
                gameHistoryList.add(i, gameHistory);
                return i;
            }
        }

        // If not added, add to end of list
        gameHistoryList.add(gameHistory);
        return gameHistoryList.size() - 1;
    }

    public void resetGameHistory(Level level) {
        this.getLevelHistoryList(level).clear();
    }

    private List<GameHistoryVo> getLevelHistoryList(Level level) {
        return switch (level) {
            case EASY -> this.easyHistoryList;
            case INTERMEDIATE -> this.intermediateHistoryList;
            case EXPERT -> this.expertHistoryList;
            case JUMBO -> this.jumboHistoryList;
        };
    }

    public void saveHistoryList(JSONObject savedHistory) throws JSONException {
        for (int levelCode = 1; levelCode <= 3; levelCode++) {
            Level level = Level.getLevelFromCode(levelCode);
            List<GameHistoryVo> historyList = this.getLevelHistoryList(level);
            JSONArray savedLevelHistory = new JSONArray();
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
        for (int levelCode = 1; levelCode <= 3; levelCode++) {
            Level level = Level.getLevelFromCode(levelCode);
            List<GameHistoryVo> historyList = singleton.getLevelHistoryList(level);  // therefore, always empty
            JSONArray savedLevelHistory = savedHistory.getJSONArray(JSONKey.getHistoryKey(level));
            for (int index = 0; index < savedLevelHistory.length(); index++) {
                historyList.add(GameHistoryVo.restoreGameHistory(savedLevelHistory.getJSONArray(index)));
            }
        }
    }

    public static GameHistoryList getInstance() {
        if (gameHistoryListInstance == null) {
            gameHistoryListInstance = new GameHistoryList();
        }
        return gameHistoryListInstance;
    }

}
