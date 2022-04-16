package com.android.myproj.minesweeper.activity.fragment.history;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.adapter.CustomHistoryAdapter;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.LogService;

public class CustomHistoryFragment extends GameHistoryFragment {

    private CustomHistoryAdapter gameHistoryAdapter;

    public CustomHistoryFragment(Activity activity, View.OnClickListener listener) {
        super(activity, listener, Level.CUSTOM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        this.rootLayout = inflater.inflate(R.layout.history_layout, container, false);
        this.rootLayout.findViewById(R.id.btn_reset_history).setOnClickListener(this.listener);
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
        this.gameHistoryAdapter = new CustomHistoryAdapter(this.activity);
        this.gameHistoryRecView.setAdapter(this.gameHistoryAdapter);
        // Change text of RESET button based on size of GameHistoryList
        this.setButtonText();
    }

    @Override
    public void notifyAdapter() {
        try {
            this.gameHistoryAdapter.notifyDataSetChanged();
            this.setButtonText();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }
    }

}
