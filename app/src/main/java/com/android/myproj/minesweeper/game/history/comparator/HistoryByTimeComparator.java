package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

// Compares GameHistoryVo objects first by time of completion, then by date
public class HistoryByTimeComparator extends AbstractHistoryComparator {

    // Sort by : Time
    // Order : Ascending
    // First, sort by time in ascending order ... #1
    // Then, sort by date in descending order (which is the default order for date) ... #2
    @Override
    public int compare(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        int comparison = this.defaultCompareTo(gameHistoryVo1, gameHistoryVo2);
        if (comparison != 0) {
            return comparison;
        }

        // #1
        comparison = compareOnlyByTime(gameHistoryVo1, gameHistoryVo2);
        if (comparison != 0) {
            return comparison;
        }

        // #2
        // gameHistoryVo1.minute == gameHistory2.minute
        // && gameHistoryVo1.second == gameHistory2.second
        // && gameHistoryVo1.millis == gameHistory2.millis
        return -1 * HistoryByDateComparator.compareOnlyByDate(gameHistoryVo1, gameHistoryVo2);
    }

    // Sort by : Time
    // Order : Descending
    // First, sort by time in descending order ... #1
    // Then, sort by date in descending order (which is the default order for date) ... #2
    @Override
    public HistoryByTimeComparator myReversed() {
        return new HistoryByTimeComparator() {
            public int compare(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
                int comparison = this.defaultCompareTo(gameHistoryVo1, gameHistoryVo2);
                if (comparison != 0) {
                    return comparison;
                }

                // #1
                comparison = -1 * compareOnlyByTime(gameHistoryVo1, gameHistoryVo2);  // by time in descending
                if (comparison != 0) {
                    return comparison;
                }

                // #2
                // gameHistoryVo1.minute == gameHistory2.minute
                // && gameHistoryVo1.second == gameHistory2.second
                // && gameHistoryVo1.millis == gameHistory2.millis
                return -1 * HistoryByDateComparator.compareOnlyByDate(gameHistoryVo1, gameHistoryVo2);
            }
        };
    }

    // Compares two GameHistoryVos only by time in ascending order
    public static int compareOnlyByTime(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (gameHistoryVo1.getMinute() < gameHistoryVo2.getMinute()) {
            return -1;
        } else if (gameHistoryVo1.getMinute() > gameHistoryVo2.getMinute()) {
            return 1;
        }

        // gameHistoryVo1.minute == gameHistory2.minute
        if (gameHistoryVo1.getSecond() < gameHistoryVo2.getSecond()) {
            return -1;
        } else if (gameHistoryVo1.getSecond() > gameHistoryVo2.getSecond()) {
            return 1;
        }

        // gameHistoryVo1.minute == gameHistory2.minute
        // && gameHistoryVo1.second == gameHistory2.second
        return gameHistoryVo1.getMillis() - gameHistoryVo2.getMillis();
        // No overflow issues because getMillis always returns an integer between 0(inclusive) and 1000(exclusive)
    }

}
