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
        this.setAdapter();

        return this.rootLayout;
    }

    @Override
    protected void setAdapter() {
        this.gameHistoryAdapter = new CustomHistoryAdapter(this.activity);
        this.gameHistoryRecView.setAdapter(this.gameHistoryAdapter);
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
