package com.android.myproj.minesweeper.game.statistics;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.StatUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class LevelStatisticsFragment extends StatisticsFragment {

    private Activity activity;
    private Level level;
    private ViewGroup container;

    public LevelStatisticsFragment(Activity activity, Level level) {
        this.activity = activity;
        this.level = level;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        this.container = (ViewGroup) inflater.inflate(R.layout.layout_statistics, container, false);
        showStat();
        return this.container;
    }

    public void showStat() {
        // Retrieve win rate as long as well
        List<Integer> stats = StatUtil.getStatistics(activity, this.level);

        // Fill Statistics (Games section) : Games Started, Games Won, Win Rate
        ((TextView) container.findViewById(R.id.tv_games_started)).setText("" + stats.get(0));
        ((TextView) container.findViewById(R.id.tv_games_won)).setText("" + stats.get(1));
        ((TextView) container.findViewById(R.id.tv_win_rate))
                .setText(new DecimalFormat("##0.0#").format((double) stats.get(2) / 100) + "%");

        // Fill Statistics (Time section) : Best Time, Avg Time
        ((TextView) container.findViewById(R.id.tv_best_time)).setText(this.formatTime(stats.get(3).intValue()));
        ((TextView) container.findViewById(R.id.tv_avg_time)).setText(this.formatTime(stats.get(4).intValue()));

        // Fill Statistics (Streak section) : Best Win Streak, Current Win Streak, Wins with No Hints
        ((TextView) container.findViewById(R.id.tv_best_streak)).setText("" + stats.get(5));
        ((TextView) container.findViewById(R.id.tv_curr_streak)).setText("" + stats.get(6));
        ((TextView) container.findViewById(R.id.tv_wins_no_hints)).setText("" + stats.get(7));
    }

    private String formatTime(int seconds) {
        int minute = seconds / 60;
        int second = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
    }

}
