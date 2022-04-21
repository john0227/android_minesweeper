package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.game.history.comparator.AbstractHistoryComparator;
import com.android.myproj.minesweeper.game.history.comparator.HistoryByDateComparator;
import com.android.myproj.minesweeper.game.history.comparator.HistoryByTimeComparator;
import com.android.myproj.minesweeper.game.logic.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Singleton
public class GameHistoryList {

    public static final int SORT_BY_TIME     = 0b1000;
    public static final int SORT_BY_DATE     = 0b0100;
    public static final int ORDER_ASCENDING  = 0b0010;
    public static final int ORDER_DESCENDING = 0b0001;

    private static final Map<Integer, Comparator<GameHistoryVo>> COMPARATOR_MAP = new HashMap<>() {{
        this.put(SORT_BY_TIME|ORDER_ASCENDING, new HistoryByTimeComparator());
        this.put(SORT_BY_TIME|ORDER_DESCENDING, new HistoryByTimeComparator().myReversed());
        this.put(SORT_BY_DATE|ORDER_ASCENDING, new HistoryByDateComparator());
        this.put(SORT_BY_DATE|ORDER_DESCENDING, new HistoryByDateComparator().myReversed());
    }};

    private static GameHistoryList gameHistoryListInstance;
    private static Comparator<GameHistoryVo> comparator;

    private final List<GameHistoryVo> easyHistoryList;
    private final List<GameHistoryVo> intermediateHistoryList;
    private final List<GameHistoryVo> expertHistoryList;
    private final List<GameHistoryVo> jumboHistoryList;
    private final List<GameHistoryVo> customHistoryList;

    private GameHistoryList() {
        this.easyHistoryList = new ArrayList<>();
        this.intermediateHistoryList = new ArrayList<>();
        this.expertHistoryList = new ArrayList<>();
        this.jumboHistoryList = new ArrayList<>();
        this.customHistoryList = new ArrayList<>();
    }

    public GameHistoryVo getGameHistory(int index, Level level) {
        if (level == Level.CUSTOM) {
            return this.customHistoryList.get(index);
        }
        return this.getLevelHistoryList(level).get(index);
    }

    public int size(Level level) {
        if (level == Level.CUSTOM) {
            return this.customHistoryList.size();
        }
        return this.getLevelHistoryList(level).size();
    }

    public void addGameHistory(GameHistoryVo gameHistory, Level level) {
        // If Custom level, do not sort history
//        if (level == Level.CUSTOM) {
//            this.customHistoryList.add((CustomHistoryVo) gameHistory);
//            return;
//        }

        List<GameHistoryVo> gameHistoryList = this.getLevelHistoryList(level);
        // Add in front of the first instance where list item is less than given gameHistory
        for (int i = 0; i < gameHistoryList.size(); i++) {
            if (comparator.compare(gameHistory, gameHistoryList.get(i)) < 0) {
                gameHistoryList.add(i, gameHistory);
                return;
            }
        }
        // If not added, add to end of list
        gameHistoryList.add(gameHistory);
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
            case CUSTOM -> this.customHistoryList;
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

    public static void restoreSavedHistory(JSONObject savedHistory) throws JSONException {
        // Do not restore data if an instance of GameHistoryList exists
        if (gameHistoryListInstance != null) {
            return;
        }

        GameHistoryList singleton = getInstance();  // always called when gameHistoryListInstance == null
        for (Level level : Level.values()) {
            List<GameHistoryVo> historyList = singleton.getLevelHistoryList(level);  // therefore, always empty
            JSONArray savedLevelHistory = savedHistory.getJSONArray(JSONKey.getHistoryKey(level));
            for (int index = 0; index < savedLevelHistory.length(); index++) {
                try {
                    historyList.add(GameHistoryVo.restoreGameHistory(savedLevelHistory.getJSONArray(index)));
                } catch (ParseException | JSONException e) {
                    continue;
                }
            }
        }
    }

    public static GameHistoryList getInstance() {
        if (gameHistoryListInstance == null) {
            gameHistoryListInstance = new GameHistoryList();
        }
        return gameHistoryListInstance;
    }

    public static void setComparator(int codeSort, int codeOrder) {
        assert codeSort == SORT_BY_TIME || codeSort == SORT_BY_DATE;
        assert codeOrder == ORDER_ASCENDING || codeOrder == ORDER_DESCENDING;

        comparator = COMPARATOR_MAP.get(codeSort | codeOrder);
        sortAllLists();
    }

    private static void sortAllLists() {
        GameHistoryList singleton = getInstance();

        List<GameHistoryVo> toSort;
        for (Level level : Level.values()) {
            toSort = singleton.getLevelHistoryList(level);
            Collections.sort(toSort, comparator);
        }
    }

}
