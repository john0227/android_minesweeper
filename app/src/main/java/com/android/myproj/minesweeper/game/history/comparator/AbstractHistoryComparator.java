package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

import java.util.Comparator;

import io.reactivex.functions.BiFunction;

abstract public class AbstractHistoryComparator implements Comparator<GameHistoryVo> {

    protected boolean compareIfWon;

    public void setCompareIfWon(boolean compareIfWon) {
        this.compareIfWon = compareIfWon;
    }

    abstract public AbstractHistoryComparator myReversed();

    protected int compareIfNull(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (gameHistoryVo1 == null) {
            return -1;
        } else if (gameHistoryVo2 == null) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareIfWon(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (!gameHistoryVo1.wasWon() || !gameHistoryVo2.wasWon()) {
            return gameHistoryVo1.getMinute() - gameHistoryVo2.getMinute();
        } else {
            return 0;
        }
    }

    private int compareIfNullAndWon(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        int comparison = compareIfNull(gameHistoryVo1, gameHistoryVo2);
        if (comparison != 0) {
            return comparison;
        }
        return compareIfWon(gameHistoryVo1, gameHistoryVo2);
    }

    protected int defaultCompareTo(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (compareIfWon) {
            return this.compareIfNullAndWon(gameHistoryVo1, gameHistoryVo2);
        }
        return this.compareIfNull(gameHistoryVo1, gameHistoryVo2);
    }

    public static AbstractHistoryComparator getDefaultCustomHistoryComparator() {
        return new HistoryByTimeComparator() {
            @Override
            public int compare(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
                int comparison = this.compareIfNull(gameHistoryVo1, gameHistoryVo2);
                if (comparison != 0) {
                    return comparison;
                }

                // #1
                comparison = -1 * HistoryByDateComparator.compareOnlyByDate(gameHistoryVo1, gameHistoryVo2);
                if (comparison != 0) {
                    return comparison;
                }

                // #2
                return HistoryByTimeComparator.compareOnlyByTime(gameHistoryVo1, gameHistoryVo2);
            }
        };
    }

}
