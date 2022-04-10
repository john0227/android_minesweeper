package com.android.myproj.minesweeper.game.history;

import com.android.myproj.minesweeper.game.logic.Game;
import com.android.myproj.minesweeper.game.logic.Level;

import java.util.ArrayList;
import java.util.List;

// Singleton
public class GameHistoryList {

    private static GameHistoryList gameHistoryListInstance;

    private final List<GameHistoryVo> easyHistoryList;
    private final List<GameHistoryVo> intermediateHistoryList;
    private final List<GameHistoryVo> expertHistoryList;

    private GameHistoryList() {
        this.easyHistoryList = new ArrayList<>();
        this.intermediateHistoryList = new ArrayList<>();
        this.expertHistoryList = new ArrayList<>();
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

        // Add in front of the first instance where list item is greater than given gameHistory
        for (int i = 0; i < gameHistoryList.size(); i++) {
            if (gameHistory.compareTo(gameHistoryList.get(i)) > 0) {
                gameHistoryList.add(i, gameHistory);
                return i;
            }
        }

        // If not added, add to end of list
        gameHistoryList.add(gameHistory);
        return gameHistoryList.size() - 1;
    }

    private List<GameHistoryVo> getLevelHistoryList(Level level) {
        return switch (level) {
            case EASY -> this.easyHistoryList;
            case INTERMEDIATE -> this.intermediateHistoryList;
            case EXPERT -> this.expertHistoryList;
        };
    }

    public static GameHistoryList getInstance() {
        if (gameHistoryListInstance == null) {
            gameHistoryListInstance = new GameHistoryList();
        }
        return gameHistoryListInstance;
    }

}
