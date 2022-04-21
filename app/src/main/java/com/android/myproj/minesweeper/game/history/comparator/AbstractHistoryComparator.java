package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

import java.util.Comparator;

abstract public class AbstractHistoryComparator implements Comparator<GameHistoryVo> {

    abstract public Comparator<GameHistoryVo> myReversed();

    protected int compareIfNull(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (gameHistoryVo1 == null) {
            return -1;
        } else if (gameHistoryVo2 == null) {
            return 1;
        } else {
            return 0;
        }
    }

    protected int compareIfWon(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (!gameHistoryVo1.wasWon() || !gameHistoryVo2.wasWon()) {
            return gameHistoryVo1.getMinute() - gameHistoryVo2.getMinute();
        } else {
            return 0;
        }
    }

    public int compareIfNullAndWon(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        int comparison = compareIfNull(gameHistoryVo1, gameHistoryVo2);
        if (comparison != 0) {
            return comparison;
        }
        return compareIfWon(gameHistoryVo1, gameHistoryVo2);
    }

}
