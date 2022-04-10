package com.android.myproj.minesweeper.activity.fragment.history;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.adapter.GameHistoryAdapter;
import com.android.myproj.minesweeper.game.logic.Level;

public class GameHistoryFragment extends Fragment {

    private final Activity activity;
    private final Level level;

    private View rootLayout;
    private RecyclerView gameHistoryRecView;
    private GameHistoryAdapter gameHistoryAdapter;

    public GameHistoryFragment(Activity activity, Level level) {
        this.activity = activity;
        this.level = level;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        this.rootLayout = inflater.inflate(R.layout.history_layout, container, false);
        this.setting();
        return this.rootLayout;
    }

    private void setting() {
        // Set up RecyclerView
        this.gameHistoryRecView = this.rootLayout.findViewById(R.id.rv_history);
        // Set LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.gameHistoryRecView.setLayoutManager(linearLayoutManager);
        // Set Adapter
        this.gameHistoryAdapter = new GameHistoryAdapter(this.activity, this.level);
        this.gameHistoryRecView.setAdapter(this.gameHistoryAdapter);
    }

}
