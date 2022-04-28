package com.android.myproj.minesweeper.activity.fragment.history;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.adapter.GameHistoryAdapter;
import com.android.myproj.minesweeper.game.history.GameHistoryList;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.LogService;

public class GameHistoryFragment extends Fragment {

    protected final Activity activity;
    protected final View.OnClickListener listener;
    protected final Level level;

    protected View rootLayout;
    protected ScrollView scrollView;
    protected RecyclerView gameHistoryRecView;
    protected GameHistoryAdapter gameHistoryAdapter;

    public GameHistoryFragment(Activity activity, View.OnClickListener listener, Level level) {
        this.activity = activity;
        this.listener = listener;
        this.level = level;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        this.rootLayout = inflater.inflate(R.layout.history_layout, container, false);
        this.rootLayout.findViewById(R.id.btn_reset_history).setOnClickListener(this.listener);

        this.setting();
        this.setAdapter();

        return this.rootLayout;
    }

    protected void setting() {
        // Initialize ScrollView
        this.scrollView = this.rootLayout.findViewById(R.id.scrollView_history);
        // Set up RecyclerView
        this.gameHistoryRecView = this.rootLayout.findViewById(R.id.rv_history);
        // Set LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.activity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.gameHistoryRecView.setLayoutManager(linearLayoutManager);
        // Change text of RESET button based on size of GameHistoryList
        this.setButtonText();
    }

    protected void setAdapter() {
        // Set Adapter
        this.gameHistoryAdapter = new GameHistoryAdapter(this.activity, this.level);
        this.gameHistoryRecView.setAdapter(this.gameHistoryAdapter);
    }

    public void notifyAdapter() {
        try {
            this.gameHistoryAdapter.notifyDataSetChanged();
            this.setButtonText();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }
    }

    public void scrollToTop() {
        try {
            this.gameHistoryRecView.setFocusable(false);
            this.scrollView.fullScroll(View.FOCUS_UP);
            this.scrollView.scrollTo(0, 0);
        } catch (NullPointerException npe) {
            LogService.warn(this.activity, "Cannot scroll to top");
        }
    }

    protected void setButtonText() {
        // Change text of RESET button based on size of GameHistoryList
        Button button = this.rootLayout.findViewById(R.id.btn_reset_history);
        if (GameHistoryList.getInstance().size(this.level) == 0) {  // If no history
            button.setText("No History");
            button.setEnabled(false);
        } else {
            button.setText("Reset History");
            button.setEnabled(true);
        }
    }

}
