package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

import java.util.Comparator;

// Compares GameHistoryVo objects first by date, then by time of completion
public class HistoryByDateComparator extends AbstractHistoryComparator {

    // Sort by : Date
    // Order : Ascending
    // First, sort by date in ascending order ... #1
    // Then, sort by time in ascending order (which is the default order for sort by time) ... #2
    @Override
    public int compare(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        int nullComparison = this.compareIfNull(gameHistoryVo1, gameHistoryVo2);
        if (nullComparison != 0) {
            return nullComparison;
        }

        // #1
        int dateComparison = compareOnlyByDate(gameHistoryVo1, gameHistoryVo2);
        if (dateComparison != 0) {
            return dateComparison;
        }

        // #2
        return HistoryByTimeComparator.onlyByTimeComparator(gameHistoryVo1, gameHistoryVo2);
    }

    // Sort by : Date
    // Order : Descending
    // First, sort by date in descending order ... #1
    // Then, sort by time in ascending order (which is the default order for sort by time) ... #2
    @Override
    public Comparator<GameHistoryVo> myReversed() {
        return (gameHistoryVo1, gameHistoryVo2) -> {
            int nullComparison = this.compareIfNull(gameHistoryVo1, gameHistoryVo2);
            if (nullComparison != 0) {
                return -1 * nullComparison;
            }

            // #1
            int dateComparison = -1 * compareOnlyByDate(gameHistoryVo1, gameHistoryVo2);  // by date in descending
            if (dateComparison != 0) {
                return dateComparison;
            }

            // #2
            return HistoryByTimeComparator.onlyByTimeComparator(gameHistoryVo1, gameHistoryVo2);
        };
    }

    // Compares two GameHistoryVos only by date in ascending order
    public static int compareOnlyByDate(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        return gameHistoryVo1.getDate().compareTo(gameHistoryVo2.getDate());
    }

}
