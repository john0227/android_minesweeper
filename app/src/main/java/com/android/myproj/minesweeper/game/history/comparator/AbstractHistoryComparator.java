package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

import java.util.Comparator;

abstract public class AbstractHistoryComparator implements Comparator<GameHistoryVo> {

    abstract public Comparator<GameHistoryVo> myReversed();

    public int compareIfNull(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (gameHistoryVo1 == null) {
            return -1;
        } else if (gameHistoryVo2 == null) {
            return 1;
        } else {
            return 0;
        }
    }

}
