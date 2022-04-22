package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.config.Key;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Singleton
public class GameHistoryList {

    public static final int SORT_BY_TIME     = 0b1000;
    public static final int SORT_BY_DATE     = 0b0100;
    public static final int ORDER_ASCENDING  = 0b0010;
    public static final int ORDER_DESCENDING = 0b0001;

    private static final Map<Integer, AbstractHistoryComparator> COMPARATOR_MAP = new HashMap<>() {{
        this.put(SORT_BY_TIME|ORDER_ASCENDING, new HistoryByTimeComparator());
        this.put(SORT_BY_TIME|ORDER_DESCENDING, new HistoryByTimeComparator().myReversed());
        this.put(SORT_BY_DATE|ORDER_ASCENDING, new HistoryByDateComparator());
        this.put(SORT_BY_DATE|ORDER_DESCENDING, new HistoryByDateComparator().myReversed());
    }};
    private static final AbstractHistoryComparator DEFAULT_CUSTOM_COMPARATOR =
            AbstractHistoryComparator.getDefaultCustomHistoryComparator();

    private static GameHistoryList gameHistoryListInstance;

    // GameHistoryVo Lists for each Level
    private final List<GameHistoryVo> easyHistoryList;
    private final List<GameHistoryVo> intermediateHistoryList;
    private final List<GameHistoryVo> expertHistoryList;
    private final List<GameHistoryVo> jumboHistoryList;
    private final List<GameHistoryVo> customHistoryList;
    // Game History with best time for each level
    private final GameHistoryVo[] bestTimeGames = new GameHistoryVo[] { null, null, null, null, null };

    // SORT and ORDER of Comparators
    // Comparators initialized to default values
    private AbstractHistoryComparator overallComparator = COMPARATOR_MAP.get(SORT_BY_TIME|ORDER_ASCENDING);
    private AbstractHistoryComparator defaultCustomComparator = DEFAULT_CUSTOM_COMPARATOR;

    private GameHistoryList() {
        this.easyHistoryList = new ArrayList<>();
        this.intermediateHistoryList = new ArrayList<>();
        this.expertHistoryList = new ArrayList<>();
        this.jumboHistoryList = new ArrayList<>();
        this.customHistoryList = new ArrayList<>();
    }

    public GameHistoryVo getGameHistory(int index, Level level) {
        return this.getLevelHistoryList(level).get(index);
    }

    public int size(Level level) {
        return this.getLevelHistoryList(level).size();
    }

    public void addGameHistory(GameHistoryVo gameHistory, Level level) {
        this.saveIfBestTime(gameHistory, level);

        AbstractHistoryComparator comparator = level != Level.CUSTOM
                ? this.overallComparator
                : this.defaultCustomComparator;

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

    private void saveIfBestTime(GameHistoryVo gameHistoryVo, Level level) {
        GameHistoryVo prevBestTime = this.bestTimeGames[level.getCode() - 1];
        if (prevBestTime == null) {
            this.bestTimeGames[level.getCode() - 1] = gameHistoryVo;
            gameHistoryVo.setIsBestTime(true);
        } else if (COMPARATOR_MAP.get(SORT_BY_TIME|ORDER_ASCENDING).compare(gameHistoryVo, prevBestTime) < 0) {
            prevBestTime.setIsBestTime(false);
            this.bestTimeGames[level.getCode() - 1] = gameHistoryVo;
            gameHistoryVo.setIsBestTime(true);
        }
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
                    GameHistoryVo gameHistoryVo = GameHistoryVo.restoreGameHistory(savedLevelHistory.getJSONArray(index));
                    historyList.add(gameHistoryVo);
                    if (gameHistoryVo.isBestTime()) {
                        singleton.saveIfBestTime(gameHistoryVo, level);
                    }
                } catch (ParseException | JSONException e) {
                    continue;
                }
            }
        }
        sortAllLists(singleton);
    }

    public static GameHistoryList getInstance() {
        if (gameHistoryListInstance == null) {
            gameHistoryListInstance = new GameHistoryList();
        }
        return gameHistoryListInstance;
    }

    public static void setOverallComparator(int codeSort, int codeOrder) {
        assert codeSort == SORT_BY_TIME || codeSort == SORT_BY_DATE;
        assert codeOrder == ORDER_ASCENDING || codeOrder == ORDER_DESCENDING;

        GameHistoryList singleton = GameHistoryList.getInstance();
        singleton.overallComparator = COMPARATOR_MAP.get(codeSort | codeOrder);
        if (singleton.defaultCustomComparator != DEFAULT_CUSTOM_COMPARATOR) {
            singleton.defaultCustomComparator = singleton.overallComparator;
        }
        sortAllLists(singleton);
    }

    public static void setCustomLevelComparator(boolean sortCustom) {
        GameHistoryList singleton = GameHistoryList.getInstance();

        AbstractHistoryComparator prev = singleton.defaultCustomComparator;
        if (sortCustom) {
            singleton.defaultCustomComparator = singleton.overallComparator;
        } else {
            singleton.defaultCustomComparator = DEFAULT_CUSTOM_COMPARATOR;
        }

        if (!prev.equals(singleton.defaultCustomComparator)) {
            sortLevelList(singleton, Level.CUSTOM);
        }
    }

    private static void sortAllLists(GameHistoryList singleton) {
        for (Level level : Level.values()) {
            sortLevelList(singleton, level);
        }
    }

    private static void sortLevelList(GameHistoryList singleton, Level level) {
        List<GameHistoryVo> toSort = singleton.getLevelHistoryList(level);
        Collections.sort(
                toSort,
                level != Level.CUSTOM ? singleton.overallComparator : singleton.defaultCustomComparator
        );
    }

    public static void notifyPreferenceChange(String key, boolean value) {
        if (key.equals(Key.PREFERENCES_SORT_CUSTOM)) {
            setCustomLevelComparator(value);
        } else if (key.equals(Key.PREFERENCES_SHOW_LOST_GAMES_BOTTOM)) {
            updateComparators(value);
        }
    }

    public static void updateComparators(boolean value) {
        for (Integer key : COMPARATOR_MAP.keySet()) {
            COMPARATOR_MAP.get(key).setCompareIfWon(value);
        }
        sortAllLists(GameHistoryList.getInstance());
    }

}
