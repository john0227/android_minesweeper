package com.android.myproj.minesweeper.activity.fragment.statistics;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.util.StatUtil;

import java.text.DecimalFormat;
import java.util.List;

public class OverallStatisticsFragment extends StatisticsFragment {

    private final Activity activity;
    private final View.OnClickListener listener;
    private ViewGroup container;

    public OverallStatisticsFragment(Activity activity, View.OnClickListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        this.container = (ViewGroup) inflater.inflate(R.layout.layout_overall_statistics, container, false);
        this.container.findViewById(R.id.btn_reset_stat).setOnClickListener(this.listener);
        showStat();
        return this.container;
    }

    public void showStat() {
        List<Integer> stats = StatUtil.getOverallStatistics(activity);

        // Fill Statistics (Games section) : Games Started, Games Won, Win Rate
        ((TextView) container.findViewById(R.id.tv_games_started)).setText("" + stats.get(0));
        ((TextView) container.findViewById(R.id.tv_games_won)).setText("" + stats.get(1));
        ((TextView) container.findViewById(R.id.tv_win_rate))
                .setText(new DecimalFormat("##0.0#").format((double) stats.get(2) / 100) + "%");
    }

}
