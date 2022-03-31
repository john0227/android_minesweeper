package com.android.myproj.minesweeper.game.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;

public class LevelStatisticsFragment extends Fragment {

    public LevelStatisticsFragment(Level level) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        // Change statistics value

        return (ViewGroup) inflater.inflate(R.layout.layout_statistics, container, false);
    }

}
