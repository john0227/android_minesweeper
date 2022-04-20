package com.android.myproj.minesweeper.game.history.comparator;

import com.android.myproj.minesweeper.game.history.GameHistoryVo;

import java.util.Comparator;

public class HistoryByTimeComparator implements Comparator<GameHistoryVo> {

    @Override
    public int compare(GameHistoryVo gameHistoryVo1, GameHistoryVo gameHistoryVo2) {
        if (gameHistoryVo1 == null) {
            return -1;
        } else if (gameHistoryVo2 == null) {
            return 1;
        }

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
        if (gameHistoryVo1.getMillis() < gameHistoryVo2.getMillis()) {
            return -1;
        } else if (gameHistoryVo1.getMillis() > gameHistoryVo2.getMillis()) {
            return 1;
        }

        // gameHistoryVo1.minute == gameHistory2.minute
        // && gameHistoryVo1.second == gameHistory2.second
        // && gameHistoryVo1.millis == gameHistory2.millis
        return gameHistoryVo1.getDate().compareTo(gameHistoryVo2.getDate());
    }

}
