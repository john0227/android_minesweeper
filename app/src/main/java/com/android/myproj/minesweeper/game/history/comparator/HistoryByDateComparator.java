package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

import java.util.Comparator;

public class HistoryByDateComparator implements Comparator<GameHistoryVo> {

    @Override
    public int compare(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (gameHistoryVo1 == null) {
            return -1;
        } else if (gameHistoryVo2 == null) {
            return 1;
        }

        int comparison = gameHistoryVo1.getDate().compareTo(gameHistoryVo2.getDate());
        if (comparison != 0) {
            return comparison;
        }

        // gameHistoryVo1.date equals gameHistoryVo2.date
        if (gameHistoryVo1.getMinute() < gameHistoryVo2.getMinute()) {
            return -1;
        } else if (gameHistoryVo1.getMinute() > gameHistoryVo2.getMinute()) {
            return 1;
        }

        // gameHistoryVo1.date equals gameHistoryVo2.date
        // && gameHistoryVo1.minute == gameHistoryVo2.minute
        if (gameHistoryVo1.getSecond() < gameHistoryVo2.getSecond()) {
            return -1;
        } else if (gameHistoryVo1.getSecond() > gameHistoryVo2.getSecond()) {
            return 1;
        }

        // gameHistoryVo1.date equals gameHistoryVo2.date
        // && gameHistoryVo1.minute == gameHistoryVo2.minute
        // && gameHistoryVo1.second == gameHistoryVo2.second
        return gameHistoryVo1.getMillis() - gameHistoryVo2.getMillis();
        // No overflow issues because getMillis always returns an integer between 0(inclusive) and 1000(exclusive)
    }

}
