package com.android.myproj.minesweeper.activity.fragment.history;

import android.app.Activity;
import android.view.View;

import com.android.myproj.minesweeper.activity.adapter.CustomHistoryAdapter;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.LogService;

public class CustomHistoryFragment extends GameHistoryFragment {

    public CustomHistoryFragment(Activity activity, View.OnClickListener listener) {
        super(activity, listener, Level.CUSTOM);
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
