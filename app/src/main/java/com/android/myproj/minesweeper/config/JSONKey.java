package com.android.myproj.minesweeper.config;

import com.android.myproj.minesweeper.game.logic.Level;

public class JSONKey {

    public static final String FILE_SAVED_DATA = "storage.json";

    public static final String KEY_EXISTS_SAVED_GAME = "exists_saved_game";
    public static final String KEY_ARRAY_IS_COVERED = "isCovered";
    public static final String KEY_ARRAY_IS_FLAGGED = "isFlagged";
    public static final String KEY_ARRAY_TILE_VALUE = "tileValue";
    public static final String KEY_COVERED_TILES = "covered_tiles";
    public static final String KEY_LEFTOVER_MINES = "leftover_mines";
    public static final String KEY_LEVEL = "game_level";
    public static final String KEY_MINUTES = "minutes";
    public static final String KEY_SECONDS = "seconds";

    public static final String KEY_EXISTS_SAVED_EASY_STAT = "exists_saved_easy_stat";
    public static final String KEY_EXISTS_SAVED_INTERMEDIATE_STAT = "exists_saved_intermediate_stat";
    public static final String KEY_EXISTS_SAVED_EXPERT_STAT = "exists_saved_expert_stat";
    // EASY Level Stats
    public static final String KEY_STAT_EASY_GAMES_STARTED = "easy_games_started";     // int
    public static final String KEY_STAT_EASY_GAMES_WON = "easy_games_won";             // int
    public static final String KEY_STAT_EASY_WIN_RATE = "easy_win_rate";               // int (0 - 100)
    public static final String KEY_STAT_EASY_BEST_TIME = "easy_best_time";             // int (MM:SS -> MM * 60 + SS)
    public static final String KEY_STAT_EASY_AVERAGE_TIME = "easy_average_time";       // int (same as above)
    public static final String KEY_STAT_EASY_BEST_STREAK = "easy_best_win_streak";     // int
    public static final String KEY_STAT_EASY_CURR_STREAK = "easy_current_win_streak";  // int
    public static final String KEY_STAT_EASY_NO_HINT_WINS = "easy_wins_with_no_hint";  // int
    // INTERMEDIATE Level Stats
    public static final String KEY_STAT_INTERMEDIATE_GAMES_STARTED = "intermediate_games_started";
    public static final String KEY_STAT_INTERMEDIATE_GAMES_WON = "intermediate_games_won";
    public static final String KEY_STAT_INTERMEDIATE_WIN_RATE = "intermediate_win_rate";
    public static final String KEY_STAT_INTERMEDIATE_BEST_TIME = "intermediate_best_time";
    public static final String KEY_STAT_INTERMEDIATE_AVERAGE_TIME = "intermediate_average_time";
    public static final String KEY_STAT_INTERMEDIATE_BEST_STREAK = "intermediate_best_win_streak";
    public static final String KEY_STAT_INTERMEDIATE_CURR_STREAK = "intermediate_current_win_streak";
    public static final String KEY_STAT_INTERMEDIATE_NO_HINT_WINS = "intermediate_wins_with_no_hint";
    // EXPERT Level Stats
    public static final String KEY_STAT_EXPERT_GAMES_STARTED = "expert_games_started";
    public static final String KEY_STAT_EXPERT_GAMES_WON = "expert_games_won";
    public static final String KEY_STAT_EXPERT_WIN_RATE = "expert_win_rate";
    public static final String KEY_STAT_EXPERT_BEST_TIME = "expert_best_time";
    public static final String KEY_STAT_EXPERT_AVERAGE_TIME = "expert_average_time";
    public static final String KEY_STAT_EXPERT_BEST_STREAK = "expert_best_win_streak";
    public static final String KEY_STAT_EXPERT_CURR_STREAK = "expert_current_win_streak";
    public static final String KEY_STAT_EXPERT_NO_HINT_WINS = "expert_wins_with_no_hint";
    // All Stat Keys
    public static final String[] KEYS_SAVED_STAT = new String[] {
            KEY_EXISTS_SAVED_EASY_STAT,
            KEY_EXISTS_SAVED_INTERMEDIATE_STAT,
            KEY_EXISTS_SAVED_EXPERT_STAT
    };
    public static final String[] KEYS_EASY_STAT = new String[] {
            KEY_STAT_EASY_GAMES_STARTED,
            KEY_STAT_EASY_GAMES_WON,
            KEY_STAT_EASY_WIN_RATE,
            KEY_STAT_EASY_BEST_TIME,
            KEY_STAT_EASY_AVERAGE_TIME,
            KEY_STAT_EASY_BEST_STREAK,
            KEY_STAT_EASY_CURR_STREAK,
            KEY_STAT_EASY_NO_HINT_WINS
    };
    public static final String[] KEYS_INTERMEDIATE_STAT = new String[]{
            KEY_STAT_INTERMEDIATE_GAMES_STARTED,
            KEY_STAT_INTERMEDIATE_GAMES_WON,
            KEY_STAT_INTERMEDIATE_WIN_RATE,
            KEY_STAT_INTERMEDIATE_BEST_TIME,
            KEY_STAT_INTERMEDIATE_AVERAGE_TIME,
            KEY_STAT_INTERMEDIATE_BEST_STREAK,
            KEY_STAT_INTERMEDIATE_CURR_STREAK,
            KEY_STAT_INTERMEDIATE_NO_HINT_WINS
    };
    public static final String[] KEYS_EXPERT_STAT = new String[] {
            KEY_STAT_EXPERT_GAMES_STARTED,
            KEY_STAT_EXPERT_GAMES_WON,
            KEY_STAT_EXPERT_WIN_RATE,
            KEY_STAT_EXPERT_BEST_TIME,
            KEY_STAT_EXPERT_AVERAGE_TIME,
            KEY_STAT_EXPERT_BEST_STREAK,
            KEY_STAT_EXPERT_CURR_STREAK,
            KEY_STAT_EXPERT_NO_HINT_WINS
    };

    private static final String[][] ALL_KEYS_BY_LEVEL = new String[][] {
        KEYS_EASY_STAT,
        KEYS_INTERMEDIATE_STAT,
        KEYS_EXPERT_STAT
    };

    public static String getStatKeyByLevel(int keyIndex, Level level) {
        return ALL_KEYS_BY_LEVEL[level.getCode() - 1][keyIndex];
    }

}
