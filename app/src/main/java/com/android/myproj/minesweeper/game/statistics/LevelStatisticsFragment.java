package com.android.myproj.minesweeper.game.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;

public class LevelStatisticsFragment extends Fragment {

    private final Level level;

    public LevelStatisticsFragment(Level level) {
        this.level = level;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        // Change statistics value

        return inflater.inflate(R.layout.layout_statistics, container, false);
    }

}
