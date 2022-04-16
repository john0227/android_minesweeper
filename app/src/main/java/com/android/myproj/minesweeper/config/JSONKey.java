package com.android.myproj.minesweeper.config;

import com.android.myproj.minesweeper.game.logic.Level;

public class JSONKey {

    public static final String FILE_SAVED_DATA = "storage.json";

    // Saved Game JSON Keys (Start)
    public static final String KEY_EXISTS_SAVED_GAME = "exists_saved_game";
    public static final String KEY_ARRAY_IS_COVERED = "isCovered";
    public static final String KEY_ARRAY_IS_FLAGGED = "isFlagged";
    public static final String KEY_ARRAY_TILE_VALUE = "tileValue";
    public static final String KEY_COVERED_TILES = "covered_tiles";
    public static final String KEY_LEFTOVER_MINES = "leftover_mines";
    public static final String KEY_LEVEL = "game_level";
    public static final String KEY_LEVEL_ROW = "game_level_row";
    public static final String KEY_LEVEL_COL = "game_level_col";
    public static final String KEY_LEVEL_MINES = "game_level_mines";
    public static final String KEY_LEVEL_DELAY = "game_level_delay";
    public static final String KEY_TIME_STORED_MILLIS = "stored_millis";
    // Saved Game JSON Keys (End)

    // Saved Statistics JSON Keys (Start)
    public static final String KEY_EXISTS_SAVED_EASY_STAT = "exists_saved_easy_stat";
    public static final String KEY_EXISTS_SAVED_INTERMEDIATE_STAT = "exists_saved_intermediate_stat";
    public static final String KEY_EXISTS_SAVED_EXPERT_STAT = "exists_saved_expert_stat";
    public static final String KEY_EXISTS_SAVED_JUMBO_STAT = "exists_saved_jumbo_stat";
    public static final String KEY_EXISTS_SAVED_CUSTOM_STAT = "exists_saved_custom_stat";
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
    // JUMBO Level Stats
    public static final String KEY_STAT_JUMBO_GAMES_STARTED = "jumbo_games_started";
    public static final String KEY_STAT_JUMBO_GAMES_WON = "jumbo_games_won";
    public static final String KEY_STAT_JUMBO_WIN_RATE = "jumbo_win_rate";
    public static final String KEY_STAT_JUMBO_BEST_TIME = "jumbo_best_time";
    public static final String KEY_STAT_JUMBO_AVERAGE_TIME = "jumbo_average_time";
    public static final String KEY_STAT_JUMBO_BEST_STREAK = "jumbo_best_win_streak";
    public static final String KEY_STAT_JUMBO_CURR_STREAK = "jumbo_current_win_streak";
    public static final String KEY_STAT_JUMBO_NO_HINT_WINS = "jumbo_wins_with_no_hint";
    // CUSTOM Level Stats
    public static final String KEY_STAT_CUSTOM_GAMES_STARTED = "custom_games_started";
    public static final String KEY_STAT_CUSTOM_GAMES_WON = "custom_games_won";
    public static final String KEY_STAT_CUSTOM_WIN_RATE = "custom_win_rate";
    public static final String KEY_STAT_CUSTOM_BEST_TIME = "custom_best_time";
    public static final String KEY_STAT_CUSTOM_AVERAGE_TIME = "custom_average_time";
    public static final String KEY_STAT_CUSTOM_BEST_STREAK = "custom_best_win_streak";
    public static final String KEY_STAT_CUSTOM_CURR_STREAK = "custom_current_win_streak";
    public static final String KEY_STAT_CUSTOM_NO_HINT_WINS = "custom_wins_with_no_hint";
    // All Stat Keys
    public static final String[] KEYS_SAVED_STAT = new String[] {
            KEY_EXISTS_SAVED_EASY_STAT,
            KEY_EXISTS_SAVED_INTERMEDIATE_STAT,
            KEY_EXISTS_SAVED_EXPERT_STAT,
            KEY_EXISTS_SAVED_JUMBO_STAT,
            KEY_EXISTS_SAVED_CUSTOM_STAT
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
    public static final String[] KEYS_JUMBO_STAT = new String[] {
            KEY_STAT_JUMBO_GAMES_STARTED,
            KEY_STAT_JUMBO_GAMES_WON,
            KEY_STAT_JUMBO_WIN_RATE,
            KEY_STAT_JUMBO_BEST_TIME,
            KEY_STAT_JUMBO_AVERAGE_TIME,
            KEY_STAT_JUMBO_BEST_STREAK,
            KEY_STAT_JUMBO_CURR_STREAK,
            KEY_STAT_JUMBO_NO_HINT_WINS
    };
    public static final String[] KEYS_CUSTOM_STAT = new String[] {
            KEY_STAT_CUSTOM_GAMES_STARTED,
            KEY_STAT_CUSTOM_GAMES_WON,
            KEY_STAT_CUSTOM_WIN_RATE,
            KEY_STAT_CUSTOM_BEST_TIME,
            KEY_STAT_CUSTOM_AVERAGE_TIME,
            KEY_STAT_CUSTOM_BEST_STREAK,
            KEY_STAT_CUSTOM_CURR_STREAK,
            KEY_STAT_CUSTOM_NO_HINT_WINS
    };

    private static final String[][] ALL_KEYS_BY_LEVEL = new String[][] {
            KEYS_EASY_STAT,
            KEYS_INTERMEDIATE_STAT,
            KEYS_EXPERT_STAT,
            KEYS_JUMBO_STAT,
            KEYS_CUSTOM_STAT
    };

    public static String getStatKey(int keyIndex, Level level) {
        return ALL_KEYS_BY_LEVEL[level.getCode() - 1][keyIndex];
    }
    // Saved Statistics JSON Keys (End)

    // Saved History JSON Keys (Start)
    public static final String KEY_EXISTS_EASY_SAVED_HISTORY = "exists_easy_saved_history";
    public static final String KEY_EXISTS_INTERMEDIATE_SAVED_HISTORY = "exists_intermediate_saved_history";
    public static final String KEY_EXISTS_EXPERT_SAVED_HISTORY = "exists_expert_saved_history";
    public static final String KEY_EXISTS_JUMBO_SAVED_HISTORY = "exists_jumbo_saved_history";
    public static final String KEY_EXISTS_CUSTOM_SAVED_HISTORY = "exists_custom_saved_history";
    public static final String KEY_EASY_SAVED_HISTORY_ARRAY = "easy_saved_history_array";
    public static final String KEY_INTERMEDIATE_SAVED_HISTORY_ARRAY = "intermediate_saved_history_array";
    public static final String KEY_EXPERT_SAVED_HISTORY_ARRAY = "expert_saved_history_array";
    public static final String KEY_JUMBO_SAVED_HISTORY_ARRAY = "jumbo_saved_history_array";
    public static final String KEY_CUSTOM_SAVED_HISTORY_ARRAY = "custom_saved_history_array";
    public static final String[] KEYS_SAVED_HISTORY = new String[] {
            KEY_EXISTS_EASY_SAVED_HISTORY,
            KEY_EXISTS_INTERMEDIATE_SAVED_HISTORY,
            KEY_EXISTS_EXPERT_SAVED_HISTORY,
            KEY_EXISTS_JUMBO_SAVED_HISTORY,
            KEY_EXISTS_CUSTOM_SAVED_HISTORY
    };
    public static String getHistoryKey(Level level) {
        return switch (level) {
            case EASY -> KEY_EASY_SAVED_HISTORY_ARRAY;
            case INTERMEDIATE -> KEY_INTERMEDIATE_SAVED_HISTORY_ARRAY;
            case EXPERT -> KEY_EXPERT_SAVED_HISTORY_ARRAY;
            case JUMBO -> KEY_JUMBO_SAVED_HISTORY_ARRAY;
            case CUSTOM -> KEY_CUSTOM_SAVED_HISTORY_ARRAY;
        };
    }
    // Saved History JSON Keys (End)

}
