package com.android.myproj.minesweeper.game.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.myproj.minesweeper.R;

public class OverallStatisticsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        return (ViewGroup) inflater.inflate(R.layout.layout_overall_statistics, container, false);
    }

}
